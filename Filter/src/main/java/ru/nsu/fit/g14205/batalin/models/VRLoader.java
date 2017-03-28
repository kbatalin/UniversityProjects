package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by kir55rus on 29.03.17.
 */
public class VRLoader {
    private EmissionModel emissionModel;
    private AbsorptionModel absorptionModel;
    private ChargeModel chargeModel;

    public void load(File file) throws IOException {
        emissionModel = new EmissionModel();
        absorptionModel = new AbsorptionModel();
        chargeModel = new ChargeModel();

        try (Scanner scanner = new Scanner(file).useLocale(Locale.US)) {
            int absorptionCount = scanner.nextInt();

            for(int i = 0; i < absorptionCount; ++i) {
                int x = scanner.nextInt();
                double y = scanner.nextDouble();
                absorptionModel.addValue(new Absorption(x, y));
            }

            int emissionCount = scanner.nextInt();

            for(int i = 0; i < emissionCount; ++i) {
                int x = scanner.nextInt();
                int red = scanner.nextInt();
                int green = scanner.nextInt();
                int blue = scanner.nextInt();
                emissionModel.addValue(new Emission(x, new Color(red, green, blue)));
            }

            int chargesCount = scanner.nextInt();

            for(int i = 0; i < chargesCount; ++i) {
                double x = scanner.nextDouble();
                double y = scanner.nextDouble();
                double z = scanner.nextDouble();
                double power = scanner.nextDouble();

                chargeModel.addCharge(new Charge(x, y, z, power));
            }

        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public EmissionModel getEmissionModel() {
        return emissionModel;
    }

    public AbsorptionModel getAbsorptionModel() {
        return absorptionModel;
    }

    public ChargeModel getChargeModel() {
        return chargeModel;
    }
}
