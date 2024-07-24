package application;

import application.controller.ApplicationController;

public class Main {
    public static void main(String[] args) {
        ApplicationController.getInstance().run();
    }
}
