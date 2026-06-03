# TA Recruitment System

TA Recruitment System 是一个基于 Java Swing 的教学助理招聘管理系统。系统面向三类用户：TA、MO（Module Organizer）和管理员，用于完成岗位发布、岗位审核、TA 资料与简历管理、岗位申请、录用处理以及工作量统计等流程。

## 功能概览

### TA

- 注册和登录 TA 账号
- 维护个人资料，包括教育背景、专业、GPA、技能、经验、可用时间和偏好岗位类型
- 上传、更新和删除 CV
- 搜索已发布岗位
- 申请岗位
- 查看自己的申请状态和拒绝原因

### MO

- 注册和登录 MO 账号
- 发布 TA 岗位
- 查看自己发布的岗位
- 查看岗位申请列表
- 录用或拒绝 TA 申请
- 岗位达到所需 TA 人数后会标记为已满

### Admin

- 注册和登录管理员账号
- 审核 MO 发布的岗位
- 批准岗位后对 TA 可见
- 拒绝岗位并记录原因
- 查看和管理用户
- 冻结或解冻用户账号
- 查看 TA 已完成工作的工作量统计

## 技术栈

- Java
- Java Swing
- Gson 2.10.1
- JSON 文件本地持久化

项目不依赖 Maven 或 Gradle，第三方依赖已放在 `lib/gson-2.10.1.jar`。

## 项目结构

```text
.
├── src/
│   ├── Main.java                 # 程序入口
│   ├── data/                     # JSON 数据读写
│   ├── gui/                      # Swing 界面
│   ├── model/                    # 领域模型
│   ├── model/enums/              # 枚举类型
│   ├── service/                  # 业务逻辑
│   └── util/                     # 密码、工作时间工具
├── lib/
│   └── gson-2.10.1.jar           # Gson 依赖
├── compile.bat                   # Windows 编译脚本
├── run.bat                       # Windows 运行脚本
├── compile.sh                    # macOS/Linux 编译脚本
└── run.sh                        # macOS/Linux 运行脚本
```

运行时会自动创建 `data/` 目录，并使用以下 JSON 文件保存数据：

- `data/users.json`
- `data/profiles.json`
- `data/cvs.json`
- `data/jobs.json`
- `data/applications.json`
- `data/feedbacks.json`
- `data/cvs/`，用于保存上传的 CV 文件

## 编译与运行

### Windows

```bat
compile.bat
run.bat
```

### macOS / Linux

```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

编译成功后，class 文件会输出到 `bin/` 目录。

也可以手动执行：

```bash
javac -cp "lib/gson-2.10.1.jar" -d bin src/model/enums/*.java src/model/*.java src/data/*.java src/service/*.java src/util/*.java src/gui/*.java src/Main.java
java -cp "lib/gson-2.10.1.jar:bin" Main
```

Windows 手动运行时 classpath 分隔符需要使用分号：

```bat
java -cp "lib\gson-2.10.1.jar;bin" Main
```

## 基本使用流程

1. 启动系统后进入登录页。
2. 点击 `Create account` 注册账号，可选择 `TA`、`MO` 或 `ADMIN` 角色。
3. MO 发布岗位后，岗位状态为 `PENDING_REVIEW`。
4. Admin 审核岗位：
   - 批准后状态变为 `PUBLISHED`，TA 可以搜索和申请。
   - 拒绝后状态变为 `REJECTED`，并保存拒绝原因。
5. TA 需要先完善个人资料并上传 CV，之后才能申请岗位。
6. MO 可以查看申请并录用或拒绝 TA。
7. 岗位录用人数达到需求人数后，状态变为 `FULL`。
8. 岗位工作时间结束后，系统会在查询岗位时自动将其标记为 `ENDED`。
9. Admin 的工作量统计只统计已经完成的工作时段。

## 主要业务规则

- 账号不可重复注册。
- 密码会通过 `PasswordUtil` 哈希后保存。
- 冻结账号无法登录。
- TA 申请岗位前必须完成个人资料并上传 CV。
- TA 不能重复申请同一个岗位。
- TA 只能申请仍处于 `PUBLISHED` 状态且工作时间尚未开始的岗位。
- MO 只能删除待审核或被拒绝的岗位。
- 工作量统计只计算已被录用且所有工作时段都已结束的岗位。

## 相关状态

用户角色：

- `TA`
- `MO`
- `ADMIN`

岗位类型：

- `ASSISTANT`
- `INVIGILATOR`

岗位状态：

- `PENDING_REVIEW`
- `PUBLISHED`
- `FULL`
- `ENDED`
- `REJECTED`

申请状态：

- `PENDING`
- `HIRED`
- `REJECTED`

## 开发说明

- 程序入口是 `src/Main.java`。
- 主窗口是 `src/gui/MainFrame.java`，使用 `CardLayout` 在登录、注册、TA 首页、MO 首页和 Admin 首页之间切换。
- 业务逻辑集中在 `src/service/`。
- 数据访问集中在 `src/data/DataStore.java` 和 `src/data/JsonDataStore.java`。
- 用户模型使用 `User` 抽象类，并由 `TA`、`MO`、`Admin` 三个子类表示具体角色。
- Gson 通过自定义反序列化器根据用户角色恢复对应的用户子类。

## 注意事项

- 首次运行时没有内置默认账号，需要通过注册页面创建用户。
- `data/` 和 `bin/` 都是运行/编译生成目录，可以根据需要清理后重新生成。
- 如果修改了源码，需要重新执行编译脚本再运行。
