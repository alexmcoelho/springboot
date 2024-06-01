package com.grupoag.springboot.domain.enums;

public enum ProjectStatus {
    OPEN(1, "Aberto"),
    RUNNING(2, "Em andamento"),
    FINISHED(3, "Finalizado");

    private int cod;
    private String description;

    private ProjectStatus(int cod, String description) {
        this.cod = cod;
        this.description = description;
    }

    public int getCod() {
        return cod;
    }

    public String getDescription() {
        return description;
    }

    public static ProjectStatus toEnum(Integer cod) {
        if(cod == null) {
            return null;
        }

        for (ProjectStatus x : ProjectStatus.values()) {
            if(cod.equals(x.getCod())) {
                return x;
            }
        }

        throw new IllegalArgumentException("Id not found: " + cod);
    }
}
