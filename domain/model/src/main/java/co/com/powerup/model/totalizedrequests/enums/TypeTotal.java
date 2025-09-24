package co.com.powerup.model.totalizedrequests.enums;

public enum TypeTotal {
    APPROVED_AMOUNT, APPROVED_REQUESTS;

    public static String changeText(String type) {
        if (APPROVED_REQUESTS.name().equals(type)) {
            return "Solicitudes aprobadas";
        } else if (APPROVED_AMOUNT.name().equals(type)) {
            return "Monto total aprobado";
        }
        return type;
    }

}
