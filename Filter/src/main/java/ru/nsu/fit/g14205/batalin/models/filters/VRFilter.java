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
        return null;
    }


}
