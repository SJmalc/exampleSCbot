import bwapi.*;

public class ExampleBot extends DefaultBWListener {
    BWClient bwClient;
    Game game;

    @Override
    public void onStart() {
        game = bwClient.getGame();
    }

    @Override
    public void onFrame() {
        Player self = game.self();
        game.drawTextScreen(100, 100, "Bot has " + self.minerals() + " minerals");
        game.drawTextScreen(100, 110, "Bot has " + self.allUnitCount() + " units");

        // Train units while we can
        for (Unit trainer : self.getUnits()) {
            UnitType unitType = trainer.getType();
            if (unitType.isBuilding() && !unitType.buildsWhat().isEmpty()) {
                UnitType toTrain = unitType.buildsWhat().get(0);
                if (game.canMake(toTrain, trainer)) {
                    trainer.train(toTrain);
                    // specific commands for training specific unit examples:
                    // commandCenter.train(UnitType.Terran_SCV);
                    // larva.morph(UnitType.Zerg_Drone);
                    // larva.morph(UnitType.Zerg_Overlord);
                }
            }
        }
    }

    // helper function for gathering minerals, checking for worker unit type
    public void workerMine(Unit unit) {
        if (unit.getType().isWorker()) {
            Unit closestMineral = null;
            int closestDistance = Integer.MAX_VALUE;
            for (Unit mineral : game.getMinerals()) {
                int distance = unit.getDistance(mineral);
                if (distance < closestDistance) {
                    closestMineral = mineral;
                    closestDistance = distance;
                }
            }
            // Gather the closest mineral
            unit.gather(closestMineral);
        }
    }

    public void onUnitComplete(Unit unit) {
        workerMine(unit);
    }

    void run() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public static void main(String[] args) {
        new ExampleBot().run();
    }
}