# mysql 도커 이미지 다운받기
docker pull mysql

# mysql 이미지 실행하기
{}안의 내용은 원하는 경우, 변경하시거나, {}를 제거하고 그대로 사용하시면 됩니다.<br>
docker run -dp {3308}:3306 -e MYSQL_ALLOW_EMPTY_PASSWORD=true -v {~/Workplaces/data/chat-service}:/var/lib/mysql --name=chat-service mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# 실행된 도커 컨테이너로 들어갑니다.
docker exec -it chat-service /bin/bash

# 도커 컨테이너 내에서 root 유저로 mysql에 접속합니다.
mysql -u root -p

# 실습에 사용할 스키마를 생성합니다.
create database chat_service;

# 실습에 사용할 mysql user를 하나 생성하고 권한을 줍니다.
create user 'service_user'@'%' identified by 'service_user';<br>
GRANT ALL PRIVILEGES ON *.* TO 'service_user'@'%';  // 편집을 열어 (*.*) 확인할 것

# 생성항 user가 잘 생성되었는지 확인합니다.
select * from mysql.user where user = 'service_user';

# mysql을 빠져나옵니다.
exit;

# 도커 컨테이너를 빠져나옵니다.
exit
