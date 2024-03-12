    #include "Wire.h" // This library allows you to communicate with I2C devices.
    const int MPU_ADDR = 0x68; // I2C address of the MPU-6050. If AD0 pin is set to HIGH, the I2C address will be 0x69.
    int16_t accelerometer_x, accelerometer_y, accelerometer_z, EjeRef; // variables for accelerometer raw data
    int16_t gyro_x, gyro_y, gyro_z,GyroUpdate; // variables for gyro raw data
    int16_t temperature=0; // variables for temperature data
    const int RED = 9, GREEN = 10, BLUE = 11, LEFT=6, RIGHT=5, SUB=3, BUTTON=8;
    int intensidad=0, boton=0;
    int buttonState = 0;   
    char tmp_str[7]; // temporary variable used in convert function
    char* convert_int16_to_str(int16_t i) { // converts int16 to string. Moreover, resulting strings will have the same length in the debug monitor.
      sprintf(tmp_str, "%6d", i);
      return tmp_str;
    }
// the setup function runs once when you press reset or power the board
void setup() {
// Todos los puertos utilizados para los LEDs son PWM.
  pinMode(SUB,OUTPUT);
  pinMode(LEFT, OUTPUT);
  pinMode(RIGHT, OUTPUT);
  pinMode(RED,OUTPUT);
  pinMode(GREEN,OUTPUT);
  pinMode(BLUE,OUTPUT);
  pinMode(BUTTON,INPUT);
  secuenciaRGB(); //Indicar que el sistema esta encendido
  Serial.begin(9600);
  Wire.begin();
  Wire.beginTransmission(MPU_ADDR); // Begins a transmission to the I2C slave (GY-521 board)
  Wire.write(0x6B); // PWR_MGMT_1 register
  Wire.write(0); // set to zero (wakes up the MPU-6050)
  Wire.endTransmission(true);
  calibrar();
}

// the loop function runs over and over again forever
void loop() {
leerBoton();
powerStandby();
   //Leer sensor
  actualizarAccel();  
  actualizarGyro();
  leerTemperatura();
}
/**
 * leerBoton():
 *  Adicionalmente se agrega un boton al chaleco
 *  el cual regula la intensidad de luz, antes de
 *  desplegar las luces se lee el valor del boton
 * @return void
 */
void leerBoton(){
//  buttonState = digitalRead(BUTTON);
    if(digitalRead(BUTTON) == 0){
     intensidad = 30;
  }
  else{
    intensidad = 208;
  }
}
/**
 * powerStandby():
 *  Secuencia estandar de luces,
 *  simula la sirena de un vehiculo.
 * 
 * @return void
 */
void powerStandby(){
    int i;

    for(i=88; i>=0; i-=5){
      analogWrite(GREEN,2);        
      analogWrite(RED,i); //Parpadeo Color Rojo gradiente verde, duracion: 15* (80/5) = 240 ms
      delay(15);
      analogWrite(GREEN,0);
    }
    analogWrite(SUB,intensidad); //Segundo Parpadeo Blanco
    delay(20); //260ms
    delay(intensidad); // 290 o 468ms
        analogWrite(SUB,0);
    for(i=80; i>=0; i-=5){
      analogWrite(BLUE,i); 
      if(i<61){
        analogWrite(GREEN,i);        
      }
          analogWrite(SUB,intensidad);
      delay(15);
    } //Parpadeo Color Azul, duracion: 210ms
        analogWrite(SUB,0);
    delay(70);
        analogWrite(SUB,intensidad);
    analogWrite(LEFT,intensidad);
    analogWrite(RIGHT,intensidad);
  delay(60);
  analogWrite(LEFT,0);
   analogWrite(RIGHT,0);
       analogWrite(SUB,0);
}

/**
 * secuenciaRGB()
 * Despliegue de los distintos colores
 * @return void
 */
void secuenciaRGB(){
  analogWrite(RED,80); //Rojo
  delay(170);
  analogWrite(BLUE,59); //Rosa = rojo y azul
  delay(170);
  analogWrite(RED,0); 
  analogWrite(GREEN,61); //Turquesa = azul y verde   
  delay(170);
  analogWrite(BLUE,0); // Verde
  delay(170);
  analogWrite(RED, 55); //Amarillo
  delay(170);
  analogWrite(RED,0);
  analogWrite(GREEN,0); 
  analogWrite(BLUE, 59); //Azul
  delay(170);
  analogWrite(BLUE,0);
  delay(150);
  analogWrite(LEFT,10);
  analogWrite(RIGHT,10);
  delay(10);
  analogWrite(LEFT,0);
  analogWrite(RIGHT,0);
  delay(250);
}

/**
 * sosRGB()
 *  Patron de emergencia que se utiliza 
 *  cuando el usuario tiene un cambio brusco
 *  en su posición, alerta de accidente.
 * @return void
 */
void sosRGB(){
  int i;
  for(i=0; i<9 ;i++){
    analogWrite(BLUE,59);
    analogWrite(GREEN,10);
    analogWrite(LEFT,130);
    analogWrite(RIGHT,130);
    analogWrite(SUB,120);
    if(i>2 && i<6){
      delay(500);
    }
    else{
      delay(130);
    }
    analogWrite(BLUE,0);
    analogWrite(GREEN,0);
    analogWrite(LEFT,0);
    analogWrite(RIGHT,0);
    analogWrite(SUB,0);
    delay(160);
  }
  delay(400);
}
/**
 * parpadeoAmarillo()
 * Activa el led para indicar vuelta hacia
 * la izquierda o hacia la derecha, mientras
 * esto ocurre el LED RGB muestra el color amarillo.
 * @return void
 */
void parpadeoAmarillo(int LED){
  int i=0;  
  //Secuencia para simular el parpadeo de la luz direccional
  for(i=0;i<6;i++){
      analogWrite(LED, 100);  
      analogWrite(SUB,100); 
      analogWrite(RED,50);
      analogWrite(GREEN,12);
      analogWrite(BLUE,1);    
      delay(400);
      analogWrite(LED,0);
      analogWrite(SUB,0);                       
      analogWrite(GREEN,0);                       
      analogWrite(RED, 0);  
      analogWrite(BLUE, 0);  
      delay(240);                           
    }
}

void leerMPU6050(){
      Wire.beginTransmission(MPU_ADDR);
      Wire.write(0x3B); // starting with register 0x3B (ACCEL_XOUT_H) [MPU-6000 and MPU-6050 Register Map and Descriptions Revision 4.2, p.40]
      Wire.endTransmission(false); // the parameter indicates that the Arduino will send a restart. As a result, the connection is kept active.
      Wire.requestFrom(MPU_ADDR, 7*2, true); // request a total of 7*2=14 registers
      // "Wire.read()<<8 | Wire.read();" means two registers are read and stored in the same variable
      accelerometer_x = Wire.read()<<8 | Wire.read(); // reading registers: 0x3B (ACCEL_XOUT_H) and 0x3C (ACCEL_XOUT_L)
      accelerometer_y = Wire.read()<<8 | Wire.read(); // reading registers: 0x3D (ACCEL_YOUT_H) and 0x3E (ACCEL_YOUT_L)
      accelerometer_z = Wire.read()<<8 | Wire.read(); // reading registers: 0x3F (ACCEL_ZOUT_H) and 0x40 (ACCEL_ZOUT_L)
      temperature = Wire.read()<<8 | Wire.read(); // reading registers: 0x41 (TEMP_OUT_H) and 0x42 (TEMP_OUT_L)
      gyro_x = Wire.read()<<8 | Wire.read(); // reading registers: 0x43 (GYRO_XOUT_H) and 0x44 (GYRO_XOUT_L)
      gyro_y = Wire.read()<<8 | Wire.read(); // reading registers: 0x45 (GYRO_YOUT_H) and 0x46 (GYRO_YOUT_L)
      gyro_z = Wire.read()<<8 | Wire.read(); // reading registers: 0x47 (GYRO_ZOUT_H) and 0x48 (GYRO_ZOUT_L)
      Serial.print(" | gY = "); Serial.println(convert_int16_to_str(gyro_y));
      Serial.print(" | gZ = "); Serial.println(convert_int16_to_str(gyro_z));
}


/**
 * solicitarDatos()
 * Cada que se solicita una lectura del sensor
 * es necesario indicar los registros que se quieren leer.
 * El RegistroInicial es la dirección de memoria a partir
 * de la cual la rutina comienza a leer.
 * @return void
 */
void solicitarDatos(int RegistroInicial,int CantidadRegistros){
     Wire.beginTransmission(MPU_ADDR); //Iniciar transmision
     Wire.write(RegistroInicial); // Empezar con el registro 
     Wire.endTransmission(false); // the parameter indicates that the Arduino will send a restart. As a result, the connection is kept active.
     Wire.requestFrom(MPU_ADDR, CantidadRegistros, true); // Solicitar N registros, donde N es igual a la cantidad total de registros a pedir
}
/**
 * leerTemperatura():
 *    en caso de que falle algun transistor por calor 
 *    la temperatura sensada tendra un valor relativamente alto.
 */

void leerTemperatura(){
      solicitarDatos(0x41, 2);
      temperature = Wire.read()<<8 | Wire.read(); // reading registers: 0x41 (TEMP_OUT_H) and 0x42 (TEMP_OUT_L)
      Serial.print(" temp ="); Serial.print(convert_int16_to_str(temperature/340.00+36.53)); Serial.println("°C");
      Serial.println("");
}
/**
 * actualizarGyro()
 * Lee el valor del giroscopio en el eje Y y en el eje Z
 * @return void
 */
void actualizarGyro(){    
      solicitarDatos(0x45,4); //Empezar a partir del registro 0x45, se solicita un total de 2*2=4 Registros.     
      GyroUpdate = Wire.read()<<8 | Wire.read(); // reading registers: 0x45 (GYRO_YOUT_H) and 0x46 (GYRO_YOUT_L). Gyro en Y
      gyro_z = Wire.read()<<8 | Wire.read(); // reading registers: 0x47 (GYRO_ZOUT_H) and 0x48 (GYRO_ZOUT_L)    
      if(gyro_z > -1900 || gyro_z < 1900 ){ //El giro alrededor de Z puede ser provocado por suelo irregular dando un valor alto en gyro_z.
        Serial.print("GyroUpdate = "); Serial.println(GyroUpdate);
        Serial.print("Gyro_z = "); Serial.println(gyro_z); 
        if(GyroUpdate>= 9900 ){ //Si el usuario gira el torso y no es provocado por una sacudida entonces:
           parpadeoAmarillo(LEFT);    
        }
        else if(GyroUpdate<= -9900){
           parpadeoAmarillo(RIGHT);
        }
      }
}

/**
 * actualizarAccel():
 *    la medicion se realiza sobre el eje Y 
 *    por la posición que tiene el sensor,
 *    sobre el eje Y recae la aceleracion de la gravedad.
 */
void actualizarAccel(){
      solicitarDatos(EjeRef, 2);
      accelerometer_y = Wire.read()<<8 | Wire.read(); // leer registros: 0x3D (ACCEL_YOUT_H) and 0x3E (ACCEL_YOUT_L)
      Serial.print("aY = "); Serial.println(convert_int16_to_str(accelerometer_y));
      while(accelerometer_y < 9000){
        sosRGB();
        actualizarAccel();
      }
}

void calibrar(){
  delay(400);
    analogWrite(RED,40);
    delay(50);
        analogWrite(RED,0);
    delay(1000);
        analogWrite(BLUE,40);
    delay(50);
        analogWrite(BLUE,0);
    delay(1000);
      solicitarDatos(0x3B, 6);      
      accelerometer_x = Wire.read()<<8 | Wire.read(); // reading registers: 0x3B (ACCEL_XOUT_H) and 0x3C (ACCEL_XOUT_L)
      accelerometer_y = Wire.read()<<8 | Wire.read(); // reading registers: 0x3D (ACCEL_YOUT_H) and 0x3E (ACCEL_YOUT_L)
      accelerometer_z = Wire.read()<<8 | Wire.read(); // reading registers: 0x3F (ACCEL_ZOUT_H) and 0x40 (ACCEL_ZOUT_L) 
      if(accelerometer_x  > accelerometer_y && accelerometer_x > accelerometer_z){
         EjeRef = 0x3B;          
      }
      else if(accelerometer_y > accelerometer_x  && accelerometer_y > accelerometer_z){
         EjeRef = 0x3D;
      }
      else{
        EjeRef = 0x3F;
      }
//      Serial.print("aRef = "); Serial.println(convert_int16_to_str(accelerometer_x));
  //          Serial.print("aRef = "); Serial.println(convert_int16_to_str(accelerometer_y));
    //              Serial.print("aRef = "); Serial.println(convert_int16_to_str(accelerometer_z));
    delay(90);
    analogWrite(GREEN,80);
    delay(180);
    analogWrite(GREEN,0);
    delay(1000);
}

