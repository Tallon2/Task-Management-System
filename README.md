# Task-Management-System
## 需求分析思路
先对系统进行模块化拆解
-  用户模块（注册 / 登录）

  - 用户注册（用户名、密码、邮箱）
  - 用户登录（JWT 签发）
  - 登录后可以查看自己的项目和任务

- 项目模块（创建 / 查看/成员管理/权限控制）

  - 创建项目
  - 查看用户所有项目列表（分页）
  - 项目成员管理（添加成员、设定角色权限：管理员 / 普通成员）
  - 权限控制：

    - 管理员可以创建/更新/删除任务

    - 普通成员只能查看任务

-  任务模块（创建 / 更新 / 删除 / 筛选/排序/依赖/Redis缓存）

  - 创建任务（名称、描述、状态、优先级、所属项目）
  - 更新任务（修改状态、优先级等）
  - 删除任务
  - 筛选任务

    - 按状态筛选

    - 按优先级筛选
  - 排序任务

    - 按优先级排序

    - 按创建时间排序

  - 任务依赖

    - 任务 A 完成后才能进行任务 B

  - Redis 缓存

    - 对任务筛选结果进行缓存，提高性能
## 数据库表设计思路
首先基础三张表 task、user、project，为了实现标记用户权限以及任务依赖关系需求，引入另外两张表，task_dependency和project—_member。（不引入新表直接在task表和project表新增字段不够灵活）
### 总体E-R图
![微信图片_20250703113446](https://github.com/user-attachments/assets/e8b77ff2-8332-4399-963d-bbc1f096348d)

### 用户表 `user`

| 字段名   | 类型        | 描述     |
| -------- | ----------- | -------- |
| id       | BIGINT (PK) | 用户ID   |
| username | VARCHAR     | 用户名   |
| password | VARCHAR     | 加密密码 |
| email    | VARCHAR     | 邮箱     |

---

### 项目表 `project`

| 字段名      | 类型        | 描述       |
| ----------- | ----------- | ---------- |
| id          | BIGINT (PK) | 项目ID     |
| name        | VARCHAR     | 项目名称   |
| description | TEXT        | 描述       |
| user_id     | BIGINT (FK) | 所属用户ID |
| created_at  | DATETIME    | 创建时间   |

---

### 任务表 `task`

| 字段名      | 类型        | 描述       |
| ----------- | ----------- | ---------- |
| id          | BIGINT (PK) | 任务ID     |
| title       | VARCHAR     | 任务名称   |
| description | TEXT        | 描述       |
| status      | VARCHAR     | 状态       |
| priority    | VARCHAR     | 优先级     |
| project_id  | BIGINT (FK) | 所属项目ID |
| created_at  | DATETIME    | 创建时间   |
| updated_at  | DATETIME    | 更新时间   |

### 项目成员表 `project_member`

| 字段名     | 类型        | 描述                       |
| ---------- | ----------- | -------------------------- |
| id         | BIGINT (PK) | 项目成员记录ID             |
| project_id | BIGINT (FK) | 所属项目ID                 |
| user_id    | BIGINT (FK) | 成员用户ID                 |
| role       | ENUM        | 成员角色（ADMIN / MEMBER） |

### 任务依赖表 `task_dependency`

| 字段名        | 类型        | 描述                   |
| ------------- | ----------- | ---------------------- |
| id            | BIGINT (PK) | 依赖记录ID             |
| task_id       | BIGINT (FK) | 当前任务ID             |
| depends_on_id | BIGINT (FK) | 当前任务所依赖的任务ID |
## api设计思路
### 用户模块

- `POST /api/auth/register`  
  用户注册。

- `POST /api/auth/login`  
  用户登录，返回 `JWT`。

---

### 项目模块

- `POST /api/projects`  
  创建项目。

- `GET /api/projects`  
  查看当前用户所有项目。

- `GET /api/projects/page`  
  分页查看项目。

- `DELETE /api/projects/{projectId}`  
  删除项目，同时删除其所有任务。

---

### 任务模块

- `POST /api/tasks`  
  创建任务。

- `PUT /api/tasks/{taskId}`  
  更新任务。

- `DELETE /api/tasks/{taskId}`  
  删除任务。

- `GET /api/tasks/project/{projectId}`  
  查看项目下所有任务。

- `GET /api/tasks/project/{projectId}/page`  
  分页查看项目下任务。

- `GET /api/tasks/project/{projectId}/filter/status`  
  根据状态筛选。

- `GET /api/tasks/project/{projectId}/filter/priority`  
  根据优先级筛选。

- `/api/tasks/project/{projectId}/sort`：
  - 按优先级 / 创建时间 排序
