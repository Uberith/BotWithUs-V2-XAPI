module BotWithUs.xapi {
    requires BotWithUs.api;
    requires BotWithUs.imgui;
    requires static lombok;
    requires com.google.gson;
    requires java.logging;
    requires org.slf4j;

    exports net.botwithus.xapi.game.inventory;
    exports net.botwithus.xapi.game.traversal.enums;
    exports net.botwithus.xapi.game.traversal;
    exports net.botwithus.xapi.query.base;
    exports net.botwithus.xapi.query.result;
    exports net.botwithus.xapi.query;
    exports net.botwithus.xapi.script.base;
    exports net.botwithus.xapi.script.permissive.interfaces;
    exports net.botwithus.xapi.script.permissive.node;
    exports net.botwithus.xapi.script.permissive.node.leaf;
    exports net.botwithus.xapi.script.permissive.base;
    exports net.botwithus.xapi.script.permissive;
    exports net.botwithus.xapi.script.task;
    exports net.botwithus.xapi.script.ui;
    exports net.botwithus.xapi.script.ui.interfaces;
    exports net.botwithus.xapi.script;
    exports net.botwithus.xapi.util.collection;
    exports net.botwithus.xapi.util.statistic;
    exports net.botwithus.xapi.util.time;
    exports net.botwithus.xapi.util;
}