# System-As-Is

## Implementation View (C4 Level 3)

The system is structured into the following **layers/packages**:

![System-As-Is Implementation View.png](System-As-Is%20Implementation%20View.png)

### Infrastructure
- **Routes**: Defines HTTP endpoints or API routes that map external requests to controllers.
- **Persistence**: Implements data access to databases and external services.

**Role in system**: Provides technical implementations and handles communication with external systems.

### Interface Adapters
- **Controllers**: Handle input from users or clients, validate it, and forward it to the application services.
- **Repositories (interfaces)**: Define contracts for data access used by application services.

**Role in system**: Acts as a bridge between the application core and external technologies.

### Application Services
- **App Services**: Orchestrate business operations by coordinating domain entities, enforcing use-case logic, and interacting with repositories.

**Role in system**: Implements the use cases of the system while remaining independent of infrastructure details.

### Domain
- **Entities**: Core business objects such as `Book`, and `Author`.
- **Domain Services**: Business logic that cannot be assigned to a single entity, e.g., calculating overdue fines.

**Role in system**: Encapsulates the core business rules, independent of frameworks or technical details.

---

## Deployment View (C4 Level 4)

![System-As-Is Deployment View.png](System-As-Is%20Deployment%20View.png)
 
---

