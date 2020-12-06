package com.izzyacademy.data.generators.services;

public class DefaultService implements DataGeneratorService {

    @Override
    public void run() {

        System.out.println("Running Default Service");

        try {

            while(true) {

                System.out.println("Default Service - waiting for 5 seconds");
                Thread.sleep(5000);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
