## Task-Management-System 设计文档

这是一个使用 **Java + Spring Boot + MyBatis-Plus** 实现的简单任务管理系统 Demo，包含：

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


---

## 🗄 数据库表设计

### 用户表 `user`
| 字段名       | 类型             | 描述       |
| ------------ | ---------------- | ---------- |
| id           | BIGINT (PK)       | 用户ID     |
| username     | VARCHAR           | 用户名     |
| password     | VARCHAR           | 加密密码   |
| email        | VARCHAR           | 邮箱       |

---

### 项目表 `project`
| 字段名       | 类型             | 描述         |
| ------------ | ---------------- | ------------ |
| id           | BIGINT (PK)       | 项目ID       |
| name         | VARCHAR           | 项目名称     |
| description  | TEXT              | 描述         |
| user_id      | BIGINT (FK)       | 所属用户ID   |
| created_at   | DATETIME          | 创建时间     |

---

### 任务表 `task`
| 字段名       | 类型             | 描述         |
| ------------ | ---------------- | ------------ |
| id           | BIGINT (PK)       | 任务ID       |
| title        | VARCHAR           | 任务名称     |
| description  | TEXT              | 描述         |
| status       | VARCHAR           | 状态         |
| priority     | VARCHAR           | 优先级       |
| project_id   | BIGINT (FK)       | 所属项目ID   |
| created_at   | DATETIME          | 创建时间     |
| updated_at   | DATETIME          | 更新时间     |

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

#### `user`（用户表）

```
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);
```

#### `project`（项目表）

```
编辑CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL,  -- 创建者
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### `project_member`（项目成员表）

```
CREATE TABLE project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('ADMIN', 'MEMBER') DEFAULT 'MEMBER'
);
```

#### `task_dependency`（任务依赖表）

```
CREATE TABLE task_dependency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '依赖记录ID',
    task_id BIGINT NOT NULL COMMENT '当前任务ID',
    depends_on_id BIGINT NOT NULL COMMENT '当前任务所依赖的任务ID',
    

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY uniq_task_dependency (task_id, depends_on_id),
    
    CONSTRAINT fk_task_dependency_task
        FOREIGN KEY (task_id) REFERENCES task(id)
        ON DELETE CASCADE,
        
    CONSTRAINT fk_task_dependency_depends_on
        FOREIGN KEY (depends_on_id) REFERENCES task(id)
        ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖表';
```

#### `task`（任务表）

```
CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20),
    priority VARCHAR(20),
    project_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);
```

---

## 🚀 API 设计

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
  

---

##  主要实现思路

###  如何拆分需求
1. **用户模块** 用于认证授权，持有 token 后可以进行项目、任务操作。
2. **项目模块** 用于组织管理任务，用户可以有多个项目。
3. **任务模块** 用于项目内的具体事务管理。

---

###  数据库设计思路
#### 总体E-R图

![image-20250703113409412](C:\Users\ASUS\AppData\Roaming\Typora\typora-user-images\image-20250703113409412.png)

------

####  用户表（`user`）

- 用于存储注册用户的基础信息，如用户名、密码（加密）、邮箱。
- 主键 `id` 唯一标识用户。
- 为后续做项目成员授权提供身份基础。

------

####  项目表（`project`）

- 用于记录用户的项目。
- 包含项目名称、描述、创建人 `user_id`。
- 一个用户可以拥有多个项目。
- 与任务、项目成员表关联。

------

####  项目成员表（`project_member`）

- 用于实现项目的权限管理。
- `user_id` 与 `project_id` 组合唯一，避免重复加入。
- 通过 `role` 区分项目管理员 (`ADMIN`) 与普通成员 (`MEMBER`)。
- 这样能方便地进行：
  - 权限校验（是否可编辑任务）
  - 项目协作管理

------

####  任务表（`task`）

- 存储项目下的任务详情，包括：
  - 标题、描述、状态（如待办、进行中、已完成）
  - 优先级（如高、中、低）
  - 创建时间、更新时间，用于排序。
- 通过 `project_id` 关联项目。
- 通过单独的 `task_dependency` 表实现任务之间的依赖关系，保证某些任务完成后才能执行。

------

####  任务依赖表（`task_dependency`）

- 用于记录任务之间的多对多依赖关系。
- `task_id` 表示当前任务。
- `depends_on_id` 表示当前任务所依赖的另一任务。
- 通过外键引用 `task` 表，删除任务时自动级联删除依赖记录。

---

###  代码实现关键点
- 使用 **MyBatis-Plus** 提供的 `ServiceImpl`，极大简化 CRUD：
  ```java
  public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService

- 通过 `lambdaQuery()` 进行条件查询：

  ```
  this.lambdaQuery().eq(User::getUsername, username).one();
  this.lambdaQuery().eq(User::getEmail, email).count();
  ```

- 使用 `JWT` 进行无状态用户认证。

- 对操作加入 `事务`（`@Transactional`）确保数据一致性。

- 使用 `统一返回封装` (`Result`) 统一前端接口返回。

- #### redis缓存实现：

- 例如：

  - `GET /api/tasks/project/{projectId}/filter?status=todo&priority=high&sort=created_at`

  由于任务筛选通常会重复（同一个用户或同一个项目成员多次筛选同样条件），引入 Redis 缓存可以显著降低数据库压力，提升响应速度。

- 具体实现：

  - 在 `TaskServiceImpl` 中使用 `@Cacheable`、`@CacheEvict`。

  - 配置 `spring.cache.type=redis` 后，Spring Boot 自动使用 Redis。

  - 在任务更新、删除后清理缓存


------

##  技术栈

- Java 17
- Spring Boot
- MyBatis-Plus
- MySQL
- Lombok
- Spring Security PasswordEncoder (加密密码)
- JWT (自定义工具类)

------

## 项目启动

1. 创建数据库，并执行 `user / project / task/project_member/task_dependency` 表的建表 SQL。

2. 配置 `application.yml` 中的数据库连接。

---

### API测试（Postman）

**POST** `{{baseUrl}}/projects`


   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

   #### Body (JSON)

   ```
   json复制编辑{
     "name": "Project A",
     "description": "示例项目"
   }
   ```

   #### Tests

   ```
   javascript复制编辑var jsonData = pm.response.json();
   pm.environment.set("projectId", jsonData.data.id);
   pm.test("项目创建成功", function () {
       pm.expect(jsonData.data.id).to.be.a('number');
   });
   ```

------

###  添加项目成员（演示管理员添加成员）

 **POST** `{{baseUrl}}/projects/{{projectId}}/members`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

   #### Body (JSON)

   ```
   json复制编辑{
     "userId": 2,
     "role": "MEMBER"
   }
   ```

------

   ###  创建任务

   **POST** `{{baseUrl}}/tasks`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

   #### Body (JSON)

   ```
   json复制编辑{
     "title": "任务 1",
     "description": "第一个任务",
     "status": "TODO",
     "priority": "HIGH",
     "projectId": {{projectId}}
   }
   ```

   #### Tests

   ```
   javascript复制编辑var jsonData = pm.response.json();
   pm.environment.set("taskId", jsonData.data.id);
   pm.test("任务创建成功", function () {
       pm.expect(jsonData.data.id).to.be.a('number');
   });
   ```

------

   ###  更新任务状态 / 优先级

   **PUT** `{{baseUrl}}/tasks/{{taskId}}`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

   #### Body (JSON)

   ```
   json复制编辑{
     "status": "IN_PROGRESS",
     "priority": "MEDIUM"
   }
   ```

------

   ### 设置任务依赖关系

   **PUT** `{{baseUrl}}/tasks/{{taskId}}/dependency`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

   #### Body (JSON)

   ```
   json复制编辑{
     "dependsOnTaskId": 123
   }
   ```

------

   ###  按状态筛选任务 + Redis 缓存测试

   **GET** `{{baseUrl}}/tasks/project/{{projectId}}/filter/status?status=TODO&pageNum=1&pageSize=5`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

------

   ###  按优先级排序任务

   **GET** `{{baseUrl}}/tasks/project/{{projectId}}/sort?orderBy=priority&pageNum=1&pageSize=5`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

------

   ### 删除任务

   **DELETE** `{{baseUrl}}/tasks/{{taskId}}`

   #### Headers

   ```
   css
   
   
   复制编辑
   Authorization: Bearer {{token}}
   ```

   