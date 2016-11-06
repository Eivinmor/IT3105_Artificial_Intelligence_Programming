
final class Settings {
    private final static int STATIC = 0, LIN = 1, EXP = 2;

    static void getSettings(SelfOrganizingMap som) {
        int learningDecayType = som.learningDecayType;
        int radiusDecayType = som.radiusDecayType;

        if (learningDecayType==EXP && radiusDecayType==EXP) {
            som.initLearningRate = 0.1;     // 0.1
            som.expLearningDecay = 0.001;   // 0.001
            som.initRadiusFactor = 1.0/2;   // 1.0/2
            som.expRadiusDecay = 0.01;      // 0.01
            som.nodesPerCity = 8;           // 8
        }

        else if (learningDecayType==LIN && radiusDecayType==LIN) {
            som.initLearningRate = 0.4;         //  0.1 for small, 0.4 for cities>300
            som.targetLinLearningRate = 0.1;    // 0.01 for small, 0.1 for cities>300
            som.initRadiusFactor = 1.0/2;
            som.targetLinRadius = 0;
            som.nodesPerCity = 2;
        }

        else if (learningDecayType==STATIC && radiusDecayType==STATIC) {
            som.initLearningRate = 0.1;
            som.initRadiusFactor = 1.0/5; // 1.0/5 on small, 1.0/10 on > 50
            som.nodesPerCity = 4;
        }


    }


}
