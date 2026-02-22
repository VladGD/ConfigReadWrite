# Config read and write

Чтение и запись параметров, значений в конфиг-файл.

## ConfigRead.java

Класс предназначен для чтения параметров и значений из конфиг-файла.


## Особенности
* После считывания значений из файла, все значения хранятся в текстовом формате.
* Поиск параметров чувствителен к регистру.
* Если нужно получить числовое значение параметра, тогда с помощью соответствующих методов строка конвертируется 
в Integer, Float. В случае проблем конвертации, выбрасывается исключение.
* Многозначные значения записываются в конфиг-файле в двойных кавычках.


## Логика работы класса ConfigRead.java

Создаем экземпляр класса ConfigRead.java
```
ConfigRead objCfg = new ConfigRead();
```

Устанавливаем настройки перед чтением конфиг-файла

1. Назначаем путь к конфиг-файлу перед сканированием.
```
objCfg.setFileConfig ("conf.cnf");
```

2. При необходимости назначаем настройки перед чтением конфиг-файла.
```
objCfg.setDelimitingSymbol (";"); // назначаем символ-разделитель для многозначных значений. По умолчанию указан символ ":".

objCfg.setFlgVerifyFileConfig (true); // устанавливаем значение, которое указывает нужно ли проверять
синтаксис конфиг-файла на наличие ошибок. По умолчанию установлено "false".

objCfg.setFlgReturnDefaultValue(true); // устанавливаем значение, которое указывает нужно ли
возвращать значение по умолчанию, если после чтения конфиг-файла при получении значения возникли ошибки. По умолчанию установлено "false".

objCfg.setFlgReturnThrowsForValue(true); // устанавливаем значение, которое указывает нужно ли
возвращать Exceptions при запросе несуществующего параметра или при выводе значения. По умолчанию установлено "false".

objCfg.setListOfRequiredParameters (hashSetParams); // указываем список параметров, которые требуется прочитать из конфиг-файла.

objCfg.setListOfRequiredParametersObjects (hashMapParams); // для всех запрашиваемых параметров создаем объект ValueObj. 
```


3. Считываем параметры из конфиг-файла.
```
objCfg.scanConfigFile(); 
```

4. Получаем значения параметров.
```
objCfg.getParameterValue ("parameter01"); // возвращает значение. Тип String
objCfg.getParameterValueInt ("parameter01"); // возвращает значение. Тип Integer
objCfg.getParameterValueFloat ("parameter01"); // возвращает значение. Тип Float
objCfg.getParameterMultiValues ("parameter01"); // возвращает список значений из параметра. Тип String
```

## Методы

### Общие методы
```
boolean scanConfigFile() - считываем параметры из конфиг-файла.

void clearListRequiredParameters () - очищаем предыдущий список запрашиваемых параметров.

void clearListParameters () - очищаем предыдущий список полученых параметров.

String getAbsolutePath () - возвращет абсолютный путь к конфиг-файлу.

String getFileConfig () - возвращает путь к конфиг-файлу, назначенный перед сканированием.

boolean checkExistFileConfig () - проверяем существование конфиг-файла.

Set<String> getListParameters () - получаем список параметров, которые смогли найти в конфиг-файле (согласно нашего списка).

boolean isExistParameter () - проверяем был ли получен параметр после чтения конфиг-файла.

boolean getErrorStatus () - проверяем наличие ошибок при последнем чтении конфиг-файла.

String getErrorText () - получаем текст последней ошибки, которая произошла при чтении конфиг-файла.

String getDelimitingSymbol () - узнаем символ-разделитель для многозначных значений.

boolean getFlgVerifyFileConfig () - проверяем нужно ли проверять синтаксис конфиг-файла на наличие ошибок.

boolean getFlgReturnThrowsForValue() - проверяем нужно ли возвращать Exceptions при запросе несуществующего параметра
или при выводе значения.

boolean getFlgReturnDefaultValue() - проверяем нужно ли возвращать значение по умолчанию, если после чтения конфиг-файла
```

### Методы позволяют установить определенные настройки перед чтением конфиг-файла.
```
boolean setFileConfig () - назначаем путь к конфиг-файлу перед сканированием

void setListOfRequiredParameters () - формируем список параметров, которые требуется прочитать из конфиг-файла.

void setListOfRequiredParametersObjects () - для всех запрашиваемых параметров создаем объект ValueObj.

void setDelimitingSymbol () - назначаем символ-разделитель для многозначных значений.

void setFlgVerifyFileConfig () - устанавливаем значение, которое указывает нужно ли проверять синтаксис
конфиг-файла на наличие ошибок.

void setFlgReturnThrowsForValue() - устанавливаем значение, которое указывает нужно ли возвращать Exceptions
при запросе несуществующего параметра или при выводе значения.

void setFlgReturnDefaultValue() - устанавливаем значение, которое указывает нужно ли возвращать значение
по умолчанию, если после чтения конфиг-файла при получении значения возникли ошибки.
```

### Методы для получение значений после чтения конфиг-файла
```
String getParameterValue () - возвращает значение. Тип String

Integer getParameterValueInt () - возвращает значение. Тип Integer

Float getParameterValueFloat () - возвращает значение. Тип Float

List<String> getParameterMultiValues () - возвращает список значений из параметра.
```


## Особенности методов

### Метод setListOfRequiredParametersObjects ()
Перед чтением конфиг-файла можно указать список параметров, которые нужно прочитать.
Дополнительно для каждого параметра можно указать требования к значениям.
Пример кода:
```
HashMap<String,HashMap<String,String>> parameters = new HashMap<>();

/*
* type - тип значения [string, int, float]. По умолчанию тип установлен string
* single - указывает однозначный или многозначное значение хранится в параметре. По умолчанию все значения single
* default - в случае ошибки при возвращении значения для параметра, будет выведено указанное значение
*/

HashMap<String,String> mapPrmVal01 = new HashMap<>();
mapPrmVal01.put("type","int"); // 
mapPrmVal01.put("default","10");
mapPrmVal01.put("single","true");
parameters.put("mapPrmVal01", mapPrmVal01);

HashMap<String,String> mapPrmVal02 = new HashMap<>();
mapPrmVal02.put("type","float");
mapPrmVal02.put("default","5.0");
parameters.put("mapPrmVal02", mapPrmVal02);

objCfg.setListOfRequiredParametersObjects(parameters);
```


## План разработки
- [ ] запись параметров и значений в файл
- [x] перед чтением файла можно установить дополнительные настройки
- [x] чтение значений из файла c конфигом








