package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.AbsorptionModel;
import ru.nsu.fit.g14205.batalin.models.ChargeModel;
import ru.nsu.fit.g14205.batalin.models.EmissionModel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 29.03.17.
 */
public class VRFilter {
    private AbsorptionModel absorptionModel;
    private EmissionModel emissionModel;
    private ChargeModel chargeModel;

    public VRFilter(AbsorptionModel absorptionModel, EmissionModel emissionModel, ChargeModel chargeModel) {
        this.absorptionModel = absorptionModel;
        this.emissionModel = emissionModel;
        this.chargeModel = chargeModel;
    }

    public BufferedImage process(BufferedImage srcImage, boolean withAbsorption, boolean withEmission, int nx, int ny, int nz) {
        double xSize = 1. / nx;
        double ySize = 1. / ny;
        double zSize = 1. / nz;

        double fmin = chargeModel.calcChargePower(0, 0, 0, xSize, ySize, zSize);
        double fmax = fmin;
        for(int x = 0; x < nx; ++x) {
            for(int y = 0; y < ny; ++y) {
                for(int z = 0; z < nz; ++z) {
                    double f = chargeModel.calcChargePower(x, y, z, xSize, ySize, zSize);
                    if (Double.compare(f, fmin) < 0) {
                        fmin = f;
                    } else if (Double.compare(f, fmax) > 0) {
                        fmax = f;
                    }
                }
            }
        }

        BufferedImage result = new BufferedImage(
                srcImage.getColorModel(),
                srcImage.copyData(srcImage.getRaster().createCompatibleWritableRaster()),
                srcImage.isAlphaPremultiplied(),
                null
        );

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                int xArea = (int)(x / xSize / 350);
                int yArea = (int)(y / xSize / 350);

                Color oldColor = new Color(result.getRGB(x, y));
                double newRed = oldColor.getRed() / 255.;
                double newGreen = oldColor.getGreen() / 255.;
                double newBlue = oldColor.getBlue() / 255.;

                for(int zArea = 0; zArea < nz; ++zArea) {
                    double f = chargeModel.calcChargePower(xArea, yArea, zArea, xSize, ySize, zSize);
                    int abscissa = getX(f, fmin, fmax);
                    double red = 0.;
                    double green = 0.;
                    double blue = 0.;
                    if(withEmission) {
                        Color color = emissionModel.calc(abscissa);
                        red = color.getRed() / 255.;
                        green = color.getGreen()/ 255.;
                        blue = color.getBlue()/ 255.;
                    }

                    double absorption = withAbsorption ? absorptionModel.calc(abscissa) : 0.;

                    newRed = (newRed * Math.exp(-absorption / nz) + red / nz);
                    newGreen = (newGreen * Math.exp(-absorption / nz) + green / nz);
                    newBlue = (newBlue * Math.exp(-absorption / nz) + blue / nz);
                }

                Color newColor = new Color(
                        ColorUtils.validate(newRed),
                        ColorUtils.validate(newGreen),
                        ColorUtils.validate(newBlue)
                );

                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private int getX(double f, double fmin, double fmax) {
        return (int)((f - fmin) / (fmax - fmin) * 100);
    }
}
