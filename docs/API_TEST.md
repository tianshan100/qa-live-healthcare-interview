# API 接口测试文档

## 1. 概述

本文档描述 qa-live-healthcare-interview 项目中所有后端 API 接口的测试流程，并指导 AI 使用 curl 命令完成接口测试。

## 2. 环境准备

### 2.1 前置条件

- JDK 17+ 已安装
- MySQL 8.0 已启动，数据库 `qa_healthcare` 已创建，`doctor` 和 `patient` 表已初始化
- 后端服务 qa-service-user 已启动在 `http://localhost:8080`

### 2.2 启动后端服务

```bash
# 设置 JAVA_HOME
export JAVA_HOME=/path/to/jdk-17

# 启动 qa-service-user
cd server/qa-service-user
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
cd D:\project\qa-live-healthcare-interview\server\qa-service-user
.\mvnw.cmd spring-boot:run
```

### 2.3 测试数据

**医生数据（doctor 表）:**

| username | password | name |
|----------|----------|------|
| dr-zhang-wei | 123456 | 张伟 |
| dr-li-na | 123456 | 李娜 |
| dr-wang-jun | 123456 | 王军 |
| dr-chen-xia | 123456 | 陈霞 |
| dr-liu-yang | 123456 | 刘洋 |

**问诊用户数据（patient 表）:**

| username | password | name |
|----------|----------|------|
| patient-zhao | 123456 | 赵明 |
| patient-sun | 123456 | 孙丽 |
| patient-zhou | 123456 | 周杰 |
| patient-wu | 123456 | 吴芳 |
| patient-zheng | 123456 | 郑浩 |

## 3. 接口列表

| 编号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| API-01 | GET | /api/doctors | 获取所有医生列表 |
| API-02 | GET | /api/doctors/active | 获取在线医生列表 |
| API-03 | GET | /api/doctors/{username} | 按用户名查询医生 |
| API-04 | POST | /api/doctors/login | 医生登录 |
| API-05 | POST | /api/patients/login | 问诊用户登录（注册即登录） |

## 4. 接口详细测试用例

---

### API-01: GET /api/doctors — 获取所有医生列表

**描述:** 返回系统中所有医生的详细信息

**请求:**

```bash
curl -s http://localhost:8080/api/doctors | python -m json.tool
```

**预期响应 (200):**

```json
[
  {
    "id": "1",
    "username": "dr-zhang-wei",
    "name": "张伟",
    "title": "主任医师",
    "department": "内科",
    "avatar": "https://randomuser.me/api/portraits/men/1.jpg",
    "experience": "20年",
    "specialties": ["高血压", "糖尿病", "心脏病"],
    "isActive": true
  }
]
```

**验证要点:**
- HTTP 状态码为 200
- 返回数组长度 >= 5
- 每个对象包含 id, username, name, title, department, avatar, experience, specialties, isActive 字段
- specialties 字段为数组类型
- id 为字符串类型

---

### API-02: GET /api/doctors/active — 获取在线医生列表

**描述:** 返回所有 isActive=true 的医生

**请求:**

```bash
curl -s http://localhost:8080/api/doctors/active | python -m json.tool
```

**预期响应 (200):**

```json
[
  {
    "id": "1",
    "username": "dr-zhang-wei",
    "name": "张伟",
    "isActive": true,
    "..."
  }
]
```

**验证要点:**
- HTTP 状态码为 200
- 每个返回对象的 isActive 字段均为 true
- 返回数量 <= API-01 返回数量

---

### API-03: GET /api/doctors/{username} — 按用户名查询医生

**测试用例 03-A: 查询存在的医生**

```bash
curl -s http://localhost:8080/api/doctors/dr-zhang-wei | python -m json.tool
```

**预期响应 (200):**

```json
{
  "id": "1",
  "username": "dr-zhang-wei",
  "name": "张伟",
  "title": "主任医师",
  "department": "内科",
  "avatar": "https://randomuser.me/api/portraits/men/1.jpg",
  "experience": "20年",
  "specialties": ["高血压", "糖尿病", "心脏病"],
  "isActive": true
}
```

**验证要点:**
- HTTP 状态码为 200
- username 字段与请求参数一致

**测试用例 03-B: 查询不存在的医生**

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors/non-existent
```

**预期响应:** HTTP 404

**验证要点:**
- HTTP 状态码为 404

---

### API-04: POST /api/doctors/login — 医生登录

**测试用例 04-A: 登录成功**

```bash
curl -s -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dr-zhang-wei","password":"123456"}' | python -m json.tool
```

**预期响应 (200):**

```json
{
  "id": "1",
  "username": "dr-zhang-wei",
  "name": "张伟",
  "title": "主任医师",
  "department": "内科",
  "avatar": "https://randomuser.me/api/portraits/men/1.jpg",
  "experience": "20年",
  "specialties": ["高血压", "糖尿病", "心脏病"],
  "isActive": true
}
```

**验证要点:**
- HTTP 状态码为 200
- 返回完整医生信息

**测试用例 04-B: 密码错误**

```bash
curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dr-zhang-wei","password":"wrongpwd"}'
```

**预期响应:** HTTP 401

**测试用例 04-C: 用户名不存在**

```bash
curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dr-noone","password":"123456"}'
```

**预期响应:** HTTP 401

---

### API-05: POST /api/patients/login — 问诊用户登录（注册即登录）

**测试用例 05-A: 已有用户登录成功**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient-zhao","password":"123456"}' | python -m json.tool
```

**预期响应 (200):**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "1",
    "username": "patient-zhao",
    "name": "赵明",
    "phone": "138****1234",
    "gender": "男"
  }
}
```

**验证要点:**
- HTTP 状态码为 200
- code 为 200
- data 中包含 id, username, name, phone, gender

**测试用例 05-B: 新用户自动注册并登录**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test-new-user","password":"123456"}' | python -m json.tool
```

**预期响应 (201):**

```json
{
  "code": 201,
  "message": "注册成功，已自动登录",
  "data": {
    "id": "6",
    "username": "test-new-user",
    "name": "test-new-user",
    "phone": "",
    "gender": ""
  }
}
```

**验证要点:**
- HTTP 状态码为 201
- code 为 201
- message 包含"注册成功"
- name 默认等于 username

**测试用例 05-C: 已有用户密码错误**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient-zhao","password":"wrong12"}' | python -m json.tool
```

**预期响应 (401):**

```json
{
  "code": 401,
  "message": "用户名或密码错误"
}
```

**验证要点:**
- HTTP 状态码为 401
- code 为 401
- 不透露具体是用户名还是密码错误

**测试用例 05-D: 用户名为空**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":"123456"}' | python -m json.tool
```

**预期响应 (400):**

```json
{
  "code": 400,
  "message": "用户名不能为空"
}
```

**测试用例 05-E: 用户名格式不合法（含特殊字符）**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"bad@user!","password":"123456"}' | python -m json.tool
```

**预期响应 (400):**

```json
{
  "code": 400,
  "message": "用户名格式不正确，需3-20位字母、数字或下划线"
}
```

**测试用例 05-F: 用户名过短（少于3位）**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","password":"123456"}' | python -m json.tool
```

**预期响应 (400):** 用户名格式不正确

**测试用例 05-G: 密码过短（少于6位）**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"12345"}' | python -m json.tool
```

**预期响应 (400):**

```json
{
  "code": 400,
  "message": "密码长度需6-20位"
}
```

**测试用例 05-H: 密码为空**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":""}' | python -m json.tool
```

**预期响应 (400):** 密码不能为空

**测试用例 05-I: 用户名含连字符（合法）**

```bash
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test-hyphen-user","password":"123456"}' | python -m json.tool
```

**预期响应 (201):** 注册成功

---

## 5. AI 测试执行指南

### 5.1 执行步骤

1. 确认后端服务已启动：`curl -s http://localhost:8080/api/doctors | head -c 100`
2. 按 API-01 ~ API-05 顺序逐个执行测试用例
3. 对每个测试用例，记录实际 HTTP 状态码和响应体
4. 将实际结果与预期结果对比，判定 PASS / FAIL
5. 将结果汇总写入测试报告 `docs/API_TEST_REPORT.md`

### 5.2 判定标准

| 项目 | 判定规则 |
|------|---------|
| HTTP 状态码 | 与预期一致则 PASS |
| 响应体结构 | 包含所有预期字段则 PASS |
| 业务逻辑 | code/message 值与预期一致则 PASS |
| 数据类型 | 字段类型（数组/字符串/布尔）与预期一致则 PASS |

### 5.3 注意事项

- 测试用例 05-B 和 05-I 会创建新数据，重复执行时用户名已存在，预期结果会从 201 变为 200（登录成功）
- 测试前建议清理测试数据或使用唯一的测试用户名
- Windows PowerShell 中 curl 为 Invoke-WebRequest 的别名，需使用 `curl.exe` 或 `Invoke-RestMethod` 替代
