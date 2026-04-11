# 🤖 AGENTS.md – Backend Instructions

## 🧠 Project Architecture
This backend MUST follow a strict layered architecture:

Controller → Service → Repository

### Rules
- Controllers handle HTTP only
- Services contain business logic
- Repositories handle database access

### ❌ NEVER:
- put business logic in controllers
- access repositories directly from controllers

---

## 🧱 Entity & DTO Rules

### DTOs are REQUIRED
- Always create DTOs for:
    - requests
    - responses

### ❌ NEVER:
- expose entities directly in controllers

### ✅ ALWAYS:
- map Entity ↔ DTO

---

## 🗄️ Database & Migrations

### Migrations are mandatory
- Every database change MUST be done via migrations

### ❌ NEVER:
- modify schema manually
- rely on auto schema generation (e.g. `ddl-auto=update`)

---

## 🖼️ Image Handling

If an entity contains images:

### Entity structure MUST include:
- `content: ByteArray`
- `contentType: string`

### Endpoint requirement:
- Provide endpoint to directly fetch image

Example: