# SassyBoot

### Development In progress

SassyBoot is a simple springboot backend ready to be used for your next sass project. 
Configured with spring-security and spring-session it can be used a backend for both 
webapp as well as mobile app.

# Installation
If you are going to use sassyboot for your next project follow below steps for installation:
- Clone the latest code `git clone --depth 1 https://github.com/chandrakantap/sassyboot.git`
- Switch to cloned repo `cd sassyboot`
- remove .git folder `rm -rf .git`
- Initialize a fresh git repository `git init`
- Create a database, as of now only postgresql supported `createdb sassyboot`
- Start local redis server, it is requires by Spring-Session
- Run the app `./gradlew clean build bootRun`

The server will up and running, listens on port `7800` by default.


# Dev Installation
For development installation follow below steps:
- Clone the latest code `git clone https://github.com/chandrakantap/sassyboot.git`
- Create a database, as of now only postgresql supported `createdb sassyboot`
- Start local redis server, it is requires by Spring-Session
- Switch to cloned repo `cd sassyboot`
- Run the app `./gradlew clean build bootRun`

# CSRF Token
All endpoints requires a CSRF token to be passed with every request in request header, 
except the endpoints which starts with `/api/public`.

Even the Login API(`POST /api/session`) requires CSRF token. To get the csrf token first time 
make a request to `GET /api/session`. It will return the CSRF token in cookie 
`Set-Cookie: XSRF-TOKEN=a788f4ff-fcb7-453a-b55e-983d8b4ad173; Path=/; [Secure]`. 
Read `XSRF-TOKEN` from cookie and send in every request header with header name `X-CSRF-TOKEN`.

CSRF cookie name and header name is configurable via `application.properties`.

```
app.security.csrf-token-header-name = X-CSRF-TOKEN
app.security.csrf-token-cookie-name = X-XSRF-TOKEN
```

After successful login CSRF token will be changed and will be updated through `Set-Cookie` response header.

# Session Management
SassyBoot use spring-security and spring-session for authentication and session management.
After successfull login a Http-Only session cookie will be set with cookie name `SESSION`.
On consecutive call this cookie will be passed by the browser and the app will recognize it.

When you are calling SassyBoot api from mobile app or any backend service, extract the session token from 
response header of loging `POST /api/session` call and then pass the token via `X-AUTH-TOKEN` request header.

The name of the request header is configurable via `application.properties`.
```
app.security.auth-token-header-name = X-AUTH-TOKEN
```

# APIs

## Login

```http
POST /api/session HTTP/1.1
Host: localhost:7800
Content-Type: application/json
X-CSRF-TOKEN: 6a36be65-b157-4582-a79a-710c89aa5766
Cookie: X-XSRF-TOKEN=<csrf token>; SESSION=<session id>

{
	"username":"<username> as of now only email id of the user is supported",
	"password":"<password>"
}
```

## Get logged user/ check if session exists

```http
GET /api/session HTTP/1.1
Host: localhost:7800
X-CSRF-TOKEN: 0c6ca4ea-9cdc-485a-97a6-1d8b60be3509
Content-Type: application/json
Cookie: X-XSRF-TOKEN=<csrf token>; SESSION=<session id>
```

## Logout

```http
DELETE /api/session HTTP/1.1
Host: localhost:7800
X-CSRF-TOKEN: 0c6ca4ea-9cdc-485a-97a6-1d8b60be3509
Content-Type: application/json
Cookie: X-XSRF-TOKEN=<csrf token>; SESSION=<session id>
```