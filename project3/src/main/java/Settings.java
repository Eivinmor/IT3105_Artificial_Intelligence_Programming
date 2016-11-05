
public final class Settings {
    private final static int STATIC = 0, LIN = 1, EXP = 2;

    public static void getSettings(SelfOrganizingMap som) {
        int learningDecayType = som.learningDecayType;
        int radiusDecayType = som.radiusDecayType;

        if (learningDecayType==EXP && radiusDecayType==EXP) {
            som.initLearningRate = 0.1;     // 0.1
            som.expLearningDecay = 0.001;   // 0.001
            som.initRadiusFactor = 1/2.0;   // 1/2.0
            som.expRadiusDecay = 0.01;      // 0.01    Trade-off between efficiency and accuracy (the higher the faster)
            som.nodesPerCity = 4;           // 4       Trade-off (obviously)
        }

        else if (learningDecayType==LIN && radiusDecayType==LIN) {
            som.initLearningRate = 1;
            som.initRadiusFactor = 1/3.0;
            som.nodesPerCity = 4;
        }


    }


}
