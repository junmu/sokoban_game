# 구현과정 상세 설명

# 1단계 : 지도 데이터 출력하기

### 1-1 프로젝트 설명
저는 먼저, 요구사항을 통해 구현해야할 개체를 다음과 같이 정리하였습니다.

- **정적 데이터**를 저장하는 개체
- **동적 데이터**를 저장하는 개체
- **데이터를 읽는** 기능

`Stage 1`, `====`, 그리고 `지도 기호`들은 이미 정해진 데이터입니다.<br>
그래서 **정적 데이터**로 분류하였습니다.<br>
그리고 이 데이터들은 여러 클래스에서 참조할 확률이 높은 **기본 자료형** 데이터라 **enum**으로 선언하였습니다.

그 다음 `지도`, `좌표`, `지도 크기`, `지도 항목 관련 각종 값들`은 **동적 데이터**로 분류하였습니다.<br>
그래서 외부로 부터 입력을 받아 값이 정해지도록 선언하였습니다.<br>
그 중에서, `지도` 같은 경우, 저장한 지도를 그대로 반환하면 원본이 훼손될 우려가 있습니다.<br>
그래서 각 스테이지에 저장된 지도는 복사하여 반환하도록 개발하였습니다.
(Stage.getCloneChrMap(), Stage.getCloneIntMap())

마지막으로 **데이터를 읽는** 기능은는 역할을 추상화 하여 Interface로 먼저 구현하고,실제 기능은 구현체를 통해 동작합니다.<br>
현재는 command line을 통해서 입력받지만, 추후 입력소스가 추가될 가능성이 있기 때문에 확장 가능한 구조로 개발하였습니다.

전체 소스는 다음과 같습니다.<br>
(설명 편의를 위해서 실제 파일순서가 아니라, 관련된 클래스 순서로 나타냅니다.)
```
ROOT
ㄴMain.java : 메인 로직을 실행함(command line 읽기 및 1단계 출력 로직 실행)

// 정적 데이터
ㄴMetaString.java : 메타데이터를 저장함(Stage 시작, Stage 종료 플래그 저장)
ㄴSign.java : sokoban 지도에 포함된 각 항목을 저장함

// 동적 데이터
ㄴPoint.java : x,y 페어를 저장하는 클래스로, 특정 좌표나 크기를 저장하는데 사용함
ㄴStage.java : Stage에 대한 다양한 정보를 저장함
ㄴStageUtils.java : Stage에 대한 유틸 기능을 담고있음

// 데이터를 읽는 기능
ㄴStageReader.java : Stage 정보를 읽는 역할을 추상화한 인터페이스로, 타 입력소스가 추가될 경우 유연한 대처를 위해 사용함
ㄴCmdStageReader.java : StageReader의 구현체로, Comma법nd line을 통해 Stage 정보를 읽어 들임
```

### 1-2 실행방법
1. 소스 클론
```shell
git clone https://gist.github.com/f3fd47327697b966f63ae4d0e28493fa.git
```
2. 자바 소스 컴파일
```shell
cd f3fd47327697b966f63ae4d0e28493fa

javac -classpath . -d build Main.java
```
3. 프로젝트 실행
```shell
# 일반 실행
java -classpath ./build Main

# Exception stack trace 출력하도록 실행
java -classpath ./build Main PRINT_ERROR
```
4. 샘플 데이터 복사
```text
Stage 1
#####
#OoP#
#####
=====
Stage 2
  #######
###  O  ###
#    o    #
# Oo P oO #
###  o  ###
 #   O  # 
 ########
```

5. 입력<br>
   4번에서 복사한 데이터를 cmd에 붙여넣고 엔터키를 입력한 다음,<br>
   **한번 더** 엔터를 입력하면 입력이 종료되고 결과가 출력됩니다.


7. 결과
```shell
> java -classpath ./build Main
Stage 1
#####
#OoP#
#####
=====
Stage 2
  #######
###  O  ###
#    o    #
# Oo P oO #
###  o  ###
 #   O  # 
 ########
 
Stage 1

#####
#OoP#
#####

가로 크기 : 5
세로 크기 : 3
구멍의 수 : 1
공의 수 : 1
플레이어 위치 (2, 4)

Stage 2

  #######
###  O  ###
#    o    #
# Oo P oO #
###  o  ###
 #   O  # 
 ########

가로 크기 : 11
세로 크기 : 7
구멍의 수 : 4
공의 수 : 4
플레이어 위치 (4, 6)


Process finished with exit code 0
```

## 2단계

## 3단계

----

Made by [@jinan159](https://github.com/jinan159)