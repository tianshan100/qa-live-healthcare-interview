# API 接口测试报告

## 1. 测试概要

| 项目 | 内容 |
|------|------|
| 测试日期 | 2026-06-01 |
| 测试环境 | Windows 11, JDK 17.0.19, MySQL 8.0.46, Spring Boot 3.5.7 |
| 后端服务 | qa-service-user @ http://localhost:8080 |
| 测试工具 | PowerShell Invoke-WebRequest / Invoke-RestMethod |
| 测试结果 | **16/16 PASS** |

## 2. 测试结果汇总

| 编号 | 接口 | 测试用例 | 预期状态码 | 实际状态码 | 结果 |
|------|------|---------|-----------|-----------|------|
| API-01 | GET /api/doctors | 获取所有医生列表 | 200 | 200 | PASS |
| API-02 | GET /api/doctors/active | 获取在线医生列表 | 200 | 200 | PASS |
| API-03A | GET /api/doctors/dr-zhang-wei | 查询存在的医生 | 200 | 200 | PASS |
| API-03B | GET /api/doctors/non-existent | 查询不存在的医生 | 404 | 404 | PASS |
| API-04A | POST /api/doctors/login | 医生登录成功 | 200 | 200 | PASS |
| API-04B | POST /api/doctors/login | 医生密码错误 | 401 | 401 | PASS |
| API-04C | POST /api/doctors/login | 医生用户名不存在 | 401 | 401 | PASS |
| API-05A | POST /api/patients/login | 问诊用户登录成功 | 200 | 200 | PASS |
| API-05B | POST /api/patients/login | 新用户自动注册 | 201 | 201 | PASS |
| API-05C | POST /api/patients/login | 问诊用户密码错误 | 401 | 401 | PASS |
| API-05D | POST /api/patients/login | 用户名为空 | 400 | 400 | PASS |
| API-05E | POST /api/patients/login | 用户名含特殊字符 | 400 | 400 | PASS |
| API-05F | POST /api/patients/login | 用户名过短(2位) | 400 | 400 | PASS |
| API-05G | POST /api/patients/login | 密码过短(5位) | 400 | 400 | PASS |
| API-05H | POST /api/patients/login | 密码为空 | 400 | 400 | PASS |
| API-05I | POST /api/patients/login | 用户名含连字符(合法) | 201 | 201 | PASS |

## 3. 详细测试记录

### API-01: GET /api/doctors

**请求:**
```
GET http://localhost:8080/api/doctors
```

**实际响应:** HTTP 200

```json
[
  {
    "id": "1",
    "username": "dr-zhang-wei",
    "name": "张伟",
    "title": "主任医师",
    "department": "内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "specialties": ["高血压", "糖尿病", "冠心病"],
    "isActive": true
  }
  // ... 共5条
]
```

**验证:**
- 返回数组长度 = 5
- 每个对象包含 id, username, name, title, department, avatar, experience, specialties, isActive
- specialties 为数组类型
- id 为字符串类型

---

### API-02: GET /api/doctors/active

**请求:**
```
GET http://localhost:8080/api/doctors/active
```

**实际响应:** HTTP 200, Content-Length: 1280

**验证:**
- 返回所有 isActive=true 的医生
- 返回数量 <= API-01 返回数量

---

### API-03A: GET /api/doctors/dr-zhang-wei

**请求:**
```
GET http://localhost:8080/api/doctors/dr-zhang-wei
```

**实际响应:** HTTP 200

**验证:** 返回张伟医生完整信息

### API-03B: GET /api/doctors/non-existent

**请求:**
```
GET http://localhost:8080/api/doctors/non-existent
```

**实际响应:** HTTP 404 NotFound

---

### API-04A: POST /api/doctors/login (登录成功)

**请求:**
```json
{"username": "dr-zhang-wei", "password": "123456"}
```

**实际响应:** HTTP 200, 返回医生完整信息

**验证:** name 字段正确返回

### API-04B: POST /api/doctors/login (密码错误)

**请求:**
```json
{"username": "dr-zhang-wei", "password": "wrongpwd"}
```

**实际响应:** HTTP 401 Unauthorized

### API-04C: POST /api/doctors/login (用户名不存在)

**请求:**
```json
{"username": "dr-noone", "password": "123456"}
```

**实际响应:** HTTP 401 Unauthorized

---

### API-05A: POST /api/patients/login (已有用户登录)

**请求:**
```json
{"username": "patient-zhao", "password": "123456"}
```

**实际响应:** HTTP 200

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

**验证:** code=200, message="登录成功", data.username="patient-zhao"

### API-05B: POST /api/patients/login (新用户自动注册)

**请求:**
```json
{"username": "test-report-{timestamp}", "password": "123456"}
```

**实际响应:** HTTP 201

```json
{
  "code": 201,
  "message": "注册成功，已自动登录",
  "data": {
    "id": "7",
    "username": "test-report-{timestamp}",
    "name": "test-report-{timestamp}",
    "phone": "",
    "gender": ""
  }
}
```

**验证:** code=201, name 默认等于 username

### API-05C: POST /api/patients/login (密码错误)

**请求:**
```json
{"username": "patient-zhao", "password": "wrong12"}
```

**实际响应:** HTTP 401

```json
{"code": 401, "message": "用户名或密码错误"}
```

**验证:** 不透露具体是用户名还是密码错误

### API-05D: POST /api/patients/login (用户名为空)

**请求:**
```json
{"username": "", "password": "123456"}
```

**实际响应:** HTTP 400

```json
{"code": 400, "message": "用户名不能为空"}
```

### API-05E: POST /api/patients/login (用户名含特殊字符)

**请求:**
```json
{"username": "bad@user!", "password": "123456"}
```

**实际响应:** HTTP 400

```json
{"code": 400, "message": "用户名格式不正确，需3-20位字母、数字或下划线"}
```

### API-05F: POST /api/patients/login (用户名过短)

**请求:**
```json
{"username": "ab", "password": "123456"}
```

**实际响应:** HTTP 400, 用户名格式不正确

### API-05G: POST /api/patients/login (密码过短)

**请求:**
```json
{"username": "testuser", "password": "12345"}
```

**实际响应:** HTTP 400

```json
{"code": 400, "message": "密码长度需6-20位"}
```

### API-05H: POST /api/patients/login (密码为空)

**请求:**
```json
{"username": "testuser", "password": ""}
```

**实际响应:** HTTP 400

```json
{"code": 400, "message": "密码不能为空"}
```

### API-05I: POST /api/patients/login (用户名含连字符)

**请求:**
```json
{"username": "test-hyphen-user", "password": "123456"}
```

**实际响应:** HTTP 201

```json
{
  "code": 201,
  "message": "注册成功，已自动登录",
  "data": {
    "id": "8",
    "username": "test-hyphen-user",
    "name": "test-hyphen-user",
    "phone": "",
    "gender": ""
  }
}
```

**验证:** 含连字符的用户名被正确接受

## 4. 结论

所有 16 个测试用例全部通过。API 行为与 PRD 文档(`docs/prd-patient-login.md`)及接口设计完全一致：

- 医生相关接口：列表查询、在线过滤、按用户名查询、登录验证均正常
- 问诊用户登录接口：已有用户登录、新用户自动注册、密码错误拦截、输入校验（空值、格式、长度）均按预期工作
- 全局异常处理器正确捕获 BusinessException 并返回结构化错误响应
