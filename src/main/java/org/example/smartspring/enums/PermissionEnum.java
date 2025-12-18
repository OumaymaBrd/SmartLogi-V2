package org.example.smartspring.enums;

public enum PermissionEnum {
    // Permissions COLIS
    COLIS_READ("Consulter les colis", "COLIS"),
    COLIS_CREATE("Créer un colis", "COLIS"),
    COLIS_UPDATE("Modifier un colis", "COLIS"),
    COLIS_DELETE("Supprimer un colis", "COLIS"),
    COLIS_UPDATE_STATUS("Modifier le statut d'un colis", "COLIS"),
    COLIS_READ_ALL("Consulter tous les colis", "COLIS"),
    COLIS_READ_OWN("Consulter ses propres colis", "COLIS"),
    COLIS_READ_ASSIGNED("Consulter les colis assignés", "COLIS"),

    // Permissions LIVREUR
    LIVREUR_READ("Consulter les livreurs", "LIVREUR"),
    LIVREUR_CREATE("Créer un livreur", "LIVREUR"),
    LIVREUR_UPDATE("Modifier un livreur", "LIVREUR"),
    LIVREUR_DELETE("Supprimer un livreur", "LIVREUR"),
    LIVREUR_MANAGE("Gérer les livreurs", "LIVREUR"),

    // Permissions ZONE
    ZONE_READ("Consulter les zones", "ZONE"),
    ZONE_CREATE("Créer une zone", "ZONE"),
    ZONE_UPDATE("Modifier une zone", "ZONE"),
    ZONE_DELETE("Supprimer une zone", "ZONE"),
    ZONE_MANAGE("Gérer les zones", "ZONE"),

    // Permissions TOURNEE
    TOURNEE_READ("Consulter les tournées", "TOURNEE"),
    TOURNEE_CREATE("Créer une tournée", "TOURNEE"),
    TOURNEE_UPDATE("Modifier une tournée", "TOURNEE"),
    TOURNEE_DELETE("Supprimer une tournée", "TOURNEE"),

    // Permissions STATISTIQUES
    STATS_VIEW("Consulter les statistiques", "STATISTIQUES"),
    STATS_EXPORT("Exporter les statistiques", "STATISTIQUES"),

    // Permissions ADMIN
    ADMIN_MANAGE_PERMISSIONS("Gérer les permissions", "ADMIN"),
    ADMIN_MANAGE_ROLES("Gérer les rôles", "ADMIN"),
    ADMIN_MANAGE_USERS("Gérer les utilisateurs", "ADMIN"),

    // Permissions DESTINATAIRE
    DESTINATAIRE_READ("Consulter les destinataires", "DESTINATAIRE"),
    DESTINATAIRE_CREATE("Créer un destinataire", "DESTINATAIRE"),

    // Permissions HISTORIQUE
    HISTORIQUE_READ("Consulter l'historique", "HISTORIQUE"),
    HISTORIQUE_READ_ALL("Consulter tout l'historique", "HISTORIQUE");

    private final String description;
    private final String category;

    PermissionEnum(String description, String category) {
        this.description = description;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
