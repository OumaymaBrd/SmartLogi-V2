package org.example.smartspring.security.enums;

import lombok.Getter;

@Getter
public enum PermissionEnum {
    // Permissions COLIS
    COLIS_CREATE("Créer un colis", "COLIS"),
    COLIS_READ_OWN("Consulter ses propres colis", "COLIS"),
    COLIS_READ_ALL("Consulter tous les colis", "COLIS"),
    COLIS_UPDATE("Modifier un colis", "COLIS"),
    COLIS_DELETE("Supprimer un colis", "COLIS"),
    COLIS_UPDATE_STATUS("Mettre à jour le statut d'un colis", "COLIS"),

    // Permissions LIVREUR
    LIVREUR_CREATE("Créer un livreur", "LIVREUR"),
    LIVREUR_READ("Consulter les livreurs", "LIVREUR"),
    LIVREUR_UPDATE("Modifier un livreur", "LIVREUR"),
    LIVREUR_DELETE("Supprimer un livreur", "LIVREUR"),
    LIVREUR_ASSIGN("Assigner des colis à un livreur", "LIVREUR"),

    // Permissions ZONE
    ZONE_CREATE("Créer une zone", "ZONE"),
    ZONE_READ("Consulter les zones", "ZONE"),
    ZONE_UPDATE("Modifier une zone", "ZONE"),
    ZONE_DELETE("Supprimer une zone", "ZONE"),

    // Permissions STATISTIQUES
    STATS_READ("Consulter les statistiques", "STATISTIQUES"),
    STATS_EXPORT("Exporter les statistiques", "STATISTIQUES"),

    // Permissions ADMIN
    ADMIN_MANAGE_USERS("Gérer les utilisateurs", "ADMIN"),
    ADMIN_MANAGE_PERMISSIONS("Gérer les permissions", "ADMIN"),
    ADMIN_MANAGE_ROLES("Gérer les rôles", "ADMIN");

    private final String description;
    private final String category;

    PermissionEnum(String description, String category) {
        this.description = description;
        this.category = category;
    }
}
