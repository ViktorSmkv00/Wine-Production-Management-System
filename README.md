# Wine-Production-Management-System


## Description
Java application with a user-friendly UI desing which helps to serve administrators, operators (winemakers), and warehouse hosts, providing various functionalities for them. 

## Participants
- **Administrator**: Primary user responsible for system management and user creation.
- **Operator (Winemaker)**: User responsible for wine production operations.
- **Warehouse Host**: User responsible for warehouse management.

## Features
### User Management
- Creation of operators by the administrator.
- Creation of warehouse hosts by the administrator.

### Wine Production Operations
- Registration of grape varieties and their quantities in the warehouse (divided into white and red categories).
- Definition of the amount of wine that can be obtained from a kilogram of grapes of a particular variety.
- Registration of types and quantities of bottles for bottling wine (750ml, 375ml, 200ml, and 187ml).
- Each wine type consists of one or several grape varieties, and a specific quantity of each is required for its production.
- Definition of the required quantities of grapes for the production of a certain type of wine.
- Bottling of wine into bottles (bonus feature: the system automatically calculates the optimal filling of bottles).

### Reports
- Stock reports for arbitrary periods:
  - Stock levels of grape varieties.
  - Availability of bottle types.
  - Stock levels of bottled wines.

### Notifications
- Critical minimum and shortage notifications for:
  - Specific grape varieties.
  - Specific bottle types.
