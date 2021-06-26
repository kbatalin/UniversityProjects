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
            String[] strData = nextData(scanner);
            int absorptionCount = Integer.parseInt(strData[0]);

            for(int i = 0; i < absorptionCount; ++i) {
                strData = nextData(scanner);
                int x = Integer.parseInt(strData[0]);
                double y = Double.parseDouble(strData[1]);
                absorptionModel.addValue(new Absorption(x, y));
            }

            strData = nextData(scanner);
            int emissionCount = Integer.parseInt(strData[0]);

            for(int i = 0; i < emissionCount; ++i) {
                strData = nextData(scanner);
                int x = Integer.parseInt(strData[0]);
                int red = Integer.parseInt(strData[1]);
                int green = Integer.parseInt(strData[2]);
                int blue = Integer.parseInt(strData[3]);
                emissionModel.addValue(new Emission(x, new Color(red, green, blue)));
            }

            strData = nextData(scanner);
            int chargesCount = Integer.parseInt(strData[0]);

            for(int i = 0; i < chargesCount; ++i) {
                strData = nextData(scanner);
                double x = Double.parseDouble(strData[0]);
                double y = Double.parseDouble(strData[1]);
                double z = Double.parseDouble(strData[2]);
                double power = Double.parseDouble(strData[3]);

                chargeModel.addCharge(new Charge(x, y, z, power));
            }

        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private static String removeComment(String line) {
        int index = line.indexOf("//");
        if (index == -1) {
            return line;
        }

        return line.substring(0, index);
    }

    private static String[] nextData(Scanner scanner) {
        String line = "";
        while (line.isEmpty()) {
            line = removeComment(scanner.nextLine()).trim();
        }

        return line.split(" ");
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
