#include <SoftwareSerial.h>

#define input1 9  //input1 és input2 a kocsi Jobb kerekeiért felelnek
#define input2 8
#define input3 6  //input3 és input4 a kocsi Bal kerekeiért felelnek
#define input4 7
#define enableA 10  //enableA azt adja meg, hogy a jobb kerekek mekkora feszültséget kapnak
#define enableB 11  //enableB azt adja meg, hogy a bal kerekek mekkora feszültséget kapnak
#define leftIR 2
#define rightIR 3
#define trigPin 5  //Trig a HC-SR04 ultrahang szenzor jelkiküldő eleme
#define echoPin 4  //Echo a HC-SR04 ultrahang szenzor vevő eleme
#define buzzerPin 12

SoftwareSerial myBluetooth(1, 13);

int szazalekosNyomatek, rightData, leftData;
long duration, distance;
long int data, newData;

void setup() {
  pinMode(rightIR, INPUT);
  pinMode(leftIR, INPUT);
  pinMode(input1, OUTPUT);
  pinMode(input2, OUTPUT);
  pinMode(enableA, OUTPUT);
  pinMode(input3, OUTPUT);
  pinMode(input4, OUTPUT);
  pinMode(enableB, OUTPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(buzzerPin, OUTPUT);
  myBluetooth.begin(9600);
}

void loop() {

  while(myBluetooth.available() == 0);

  do{
      data = myBluetooth.read();

      switch(data){
        case 'F':
          forwardMotion(100);
          break;
        case 'R':
          leftMotion(100);
          break;          
        case 'L':
          rightMotion(100);
          break;
        case 'B':
          backwardMotion(100);
          break;
        case 'S':
          stopMotion();
          break;
        case 'A':
          do{
            distance = measureDistance();

            if (distance < 20)
            {
              stopMotion();
              digitalWrite(buzzerPin, HIGH);
            }
            else{
              rightData = digitalRead(rightIR);
              leftData = digitalRead(leftIR);

              if(rightData == 1 && leftData == 1)
              {
                stopMotion();
              }
              else if(rightData == 0 && leftData == 1){
                rightMotion(70);
              }
              else if(rightData == 1 && leftData == 0){
                leftMotion(70);
              }
              else if(rightData == 0 && leftData == 0){
                forwardMotion(70);
              }
              else
                stopMotion();
              }
              newData = myBluetooth.read();
            }while(newData != 'Q');
       default:
       break;
      }
    }while(data != 'E');
}

void forwardMotion(int nyomatek) {
  szazalekosNyomatek = map(nyomatek,0,100,0,255);
  
  digitalWrite(input2, LOW);
  digitalWrite(input1, HIGH);
  digitalWrite(input3, LOW);
  digitalWrite(input4, HIGH);
  analogWrite(enableA, szazalekosNyomatek);
  analogWrite(enableB, szazalekosNyomatek);
}

void backwardMotion(int nyomatek) {
  szazalekosNyomatek = map(nyomatek,0,100,0,255);
  
  digitalWrite(input2, HIGH);
  digitalWrite(input1, LOW);
  digitalWrite(input3, HIGH);
  digitalWrite(input4, LOW);
  analogWrite(enableA, szazalekosNyomatek);
  analogWrite(enableB, szazalekosNyomatek);
}

void leftMotion(int nyomatek) {
  szazalekosNyomatek = map(nyomatek,0,100,0,255);
  
  digitalWrite(input2, LOW);
  digitalWrite(input1, HIGH);
  digitalWrite(input3, HIGH);
  digitalWrite(input4, LOW);
  analogWrite(enableA, szazalekosNyomatek);
  analogWrite(enableB, szazalekosNyomatek);
}

void rightMotion(int nyomatek) {
  szazalekosNyomatek = map(nyomatek,0,100,0,255);
  
  digitalWrite(input2, HIGH);
  digitalWrite(input1, LOW);
  digitalWrite(input3, LOW);
  digitalWrite(input4, HIGH);
  analogWrite(enableA, szazalekosNyomatek);
  analogWrite(enableB, szazalekosNyomatek);
}

void stopMotion(){
  digitalWrite(enableA, LOW);
  digitalWrite(enableB, LOW);
}

long measureDistance(){
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  duration = pulseIn(echoPin, HIGH);

  distance = duration * 0.034 / 2;  //A jel által megtett távolságot a hang levegőben lévő terjedési sebessége segítségével határozzuk meg.

  return distance;
}
