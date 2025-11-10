#!/bin/bash

# 오류나면 멈추고, 없는 변수 쓰면 에러, 파이프 중간 실패도 감지
set -euo pipefail

# 프로젝트 경로
PROJECT_HOME="/var/www/jsp.servlet.localhost"

# 이전 클래스 파일 삭제
rm -rf "$PROJECT_HOME/WEB-INF/classes"

# 새 클래스 디렉터리 생성
mkdir -p "$PROJECT_HOME/WEB-INF/classes"

# Java 파일 컴파일
javac -encoding UTF-8 \
  -cp /usr/share/tomcat10/lib/servlet-api.jar:"$PROJECT_HOME/WEB-INF/classes":"$PROJECT_HOME/WEB-INF/lib/*" \
  -d "$PROJECT_HOME/WEB-INF/classes" \
  $(find "$PROJECT_HOME/WEB-INF/src/" -name "*.java")

# Tomcat 서버 재시작
sudo systemctl restart tomcat10
