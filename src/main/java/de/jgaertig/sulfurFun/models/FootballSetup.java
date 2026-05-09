package de.jgaertig.sulfurFun.models;

public class FootballSetup implements GameSetup {
    @Override
    public String getType() { return "football"; }

    @Override
    public String[] getSteps() {
        return new String[]{"bluegoal1", "bluegoal2", "blueplayerspawn", "redgoal1", "redgoal2", "redplayerspawn", "ballspawn", "playingtime", "maxplayer", "balls", "touchball", "breakingblocks", "placingblocks", "countdown", "goalswin"};
    }

    @Override
    public String[] getMessages() {
        return new String[]{
                "messages.newgame.football.step1",
                "messages.newgame.football.step2",
                "messages.newgame.football.step3",
                "messages.newgame.football.step4",
                "messages.newgame.football.step5",
                "messages.newgame.football.step6",
                "messages.newgame.football.step7",
                "messages.newgame.football.step8",
                "messages.newgame.football.step9",
                "messages.newgame.football.step10",
                "messages.newgame.football.step11",
                "messages.newgame.football.step12",
                "messages.newgame.football.step13",
                "messages.newgame.football.step14",
                "messages.newgame.football.step15"
        };
    }

    @Override
    public StepType[] getStepTypes() {
        return new StepType[]{
                StepType.CLICK, StepType.CLICK, StepType.CLICK,
                StepType.CLICK, StepType.CLICK, StepType.CLICK,
                StepType.CLICK,
                StepType.CHAT, StepType.CHAT, StepType.CHAT, StepType.CHAT, StepType.CHAT, StepType.CHAT, StepType.CHAT, StepType.CHAT
        };
    }

    // Yes/No: "^(yes|no)$" | Zahlen ab 1: "^[1-9][0-9]*$" | egal: null
    @Override
    public String[] getInputPatterns() {
        return new String[] {
                null, null, null,
                null, null, null,
                null,
                "^[1-9][0-9]+$",
                "^[1-9][0-9]*$",
                "^[1-9][0-9]*$",
                "^(yes|no)$",
                "^(yes|no)$",
                "^(yes|no)$",
                "^[0-9]|[1-9][0-9]+$",
                "^[0-9]+$"
        };
    }


}