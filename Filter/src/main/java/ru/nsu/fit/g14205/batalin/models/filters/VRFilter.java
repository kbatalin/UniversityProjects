package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.AbsorptionModel;
import ru.nsu.fit.g14205.batalin.models.ChargeModel;
import ru.nsu.fit.g14205.batalin.models.EmissionModel;

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
        return onlyAbsorption(srcImage, nx, ny, nz);
    }

    private BufferedImage onlyAbsorption(BufferedImage srcImage, int nx, int ny, int nz) {
        BufferedImage result = new BufferedImage(
                srcImage.getColorModel(),
                srcImage.copyData(srcImage.getRaster().createCompatibleWritableRaster()),
                srcImage.isAlphaPremultiplied(),
                null
        );
        int[] zBuffer = new int[nx * ny];

        double xSize = 350. / nx;
        double ySize = 350. / ny;
//        double zSize =


        System.out.println(absorptionModel.calc(20));
        System.out.println(absorptionModel.calc(30));
        System.out.println(absorptionModel.calc(35));
        System.out.println(absorptionModel.calc(40));
        System.out.println(absorptionModel.calc(41));
        System.out.println(absorptionModel.calc(50));

        return null;
    }
}
