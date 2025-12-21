# Smart Delivery Management System (SDMS)

![Docker Logo](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![Loki Logo](https://img.shields.io/badge/Loki-A500FF?style=for-the-badge&logo=grafanaloki&logoColor=white) ![SLF4j Logo](https://img.shields.io/badge/SLF4J-000?style=for-the-badge&logo=slf4j&logoColor=white) ![Spring Boot Logo](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Grafana Logo](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white) ![Promtail Logo](https://img.shields.io/badge/Promtail-A500FF?style=for-the-badge&logo=grafanaloki&logoColor=white) ![Lombok Logo](https://img.shields.io/badge/Lombok-FF338D?style=for-the-badge&logo=lombok&logoColor=white) ![Liquibase Logo](https://img.shields.io/badge/Liquibase-383838?style=for-the-badge&logo=liquibase&logoColor=white) ![MapStruct Logo](https://img.shields.io/badge/MapStruct-383838?style=for-the-badge&logo=mapstruct&logoColor=white) ![Maven Logo](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![ApiDog](https://res.cloudinary.com/daog6scxm/image/upload/v1692367185/cms/api-management-tools/APIDog_logo_pmz2uv.webp)

---
# Architecture de Logging et de Développement Backend

![Presnetation](images/architecture.png)


## Diagramme du Classe 

![Diagramme Classe](images/diagramme-classe-V2.png)

## Présentation des interfaces Grafana
- **Statistiques** - Cette partie présente la vue statique de tous les logs générés

![Partie 1 Grafana ](images/Grafana-interface-1.png)

- **Graphiques** - Cette section est dédiée aux graphiques


![Partie 2 Grafana ](images/Grafana-interface-2.png)

- **Details Logs** - Cette section affiche la traçabilité détaillée de chaque log


![Partie 3 Grafana ](images/Grafana-interface-3.png)


## Fonctionnalités

### Gestion des Entités
- **ClientExpediteur** - Gestion des clients expéditeurs
- **Destinataire** - Gestion des destinataires
- **Livreur** - Gestion des livreurs
- **Zone** - Gestion des zones de livraison
- **Colis** - Gestion des colis avec statuts et priorités
- **Produit** - Gestion des produits
- **HistoriqueLivraison** - Traçabilité complète des colis

### API REST
- CRUD complet pour toutes les entités
- Pagination et tri
- Recherche par mots-clés
- Filtrage par statut, priorité, zone, ville
- Statistiques par livreur et zone

## Endpoints API

### Clients Expéditeurs
- `POST /api/clients-expediteurs` - Créer un client
- `GET /api/clients-expediteurs` - Liste paginée
- `GET /api/clients-expediteurs/{id}` - Détails
- `PUT /api/clients-expediteurs/{id}` - Modifier
- `DELETE /api/clients-expediteurs/{id}` - Supprimer
- `GET /api/clients-expediteurs/search?keyword=` - Rechercher

### Colis
- `POST /api/colis` - Créer un colis
- `GET /api/colis` - Liste paginée
- `GET /api/colis/{id}` - Détails
- `PUT /api/colis/{id}` - Modifier
- `DELETE /api/colis/{id}` - Supprimer
- `GET /api/colis/statut/{statut}` - Filtrer par statut
- `GET /api/colis/priorite/{priorite}` - Filtrer par priorité
- `GET /api/colis/livreur/{livreurId}` - Colis d'un livreur
- `GET /api/colis/livreur/{livreurId}/stats` - Statistiques livreur
- `GET /api/colis/zone/{zoneId}/stats` - Statistiques zone


## Logs

L'application utilise SLF4J avec Logback pour les logs:
- **DEBUG** - Opérations de lecture
- **INFO** - Opérations de création/modification/suppression
- **ERROR** - Erreurs
- **EMAIL** - Suivi des emails envoyés

