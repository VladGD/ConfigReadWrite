package confrw;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Description: Reading parameters and values from a config file
 * @author VladGD
 * @version 1.0
 */

public class ConfigRead {

    /**
     * Храним абсолютный путь к конфиг-файлу.
     */
    private String fileConfig = "";

    /**
     * Сохраняем текст ошибки.
     */
    private String messageErr = "";

    /**
     * Символ-разделитель для многозначных значений в параметре.
     */
    private String delimitingSymbol = ":";

    /**
     * Флаг. Нужно ли проверять конфиг-файл на наличие ошибок в синтаксисе.
     */
    private boolean flgVerifyFileConfig = false;

    /**
     * Флаг. Нужно ли возвращать Exceptions при запросе несуществующего параметра или при выводе значения.
     * При выводе значения после сканирования, может возникнуть ситуация, что запрашиваемый параметр не
     * найден в конфиг-файле или запрашиваемое значение не может быть выведено в нужном типе (int, float)
     */
    private boolean flgReturnThrowsForValue = false;

    /**
     * Флаг. Нужно ли возвращать значение по умолчанию если после сканирования при получении значения возникли ошибки.
     */
    private boolean flgReturnDefaultValue = false;

    /**
     * Объект конфиг-файла.
     */
    private File objFile = null;

    /**
     * Храним строки из конфиг-файла.
     */
    private final List<String> lineFileConf = new LinkedList<>();

    /**
     * Содержит список запрашиваемых параметры. ValueObj - хранятся настройки и атрибуты для параметров.
     */
    private final HashMap<String, ValueObj> mapRequiredParameters = new HashMap<>();

    /**
     * Содержит список полученых параметров из конфиг-файла. ValueObj - хранятся настройки и атрибуты для параметров.
     */
    private final HashMap<String, ValueObj> mapParametersValues = new HashMap<>();

    ConfigRead () {

    }

    /**
     * Считываем параметры из конфиг-файла.
     * @return boolean - [true] - успешное выполнение
     */
    public boolean scanConfigFile() throws RuntimeException {

        try {
            //проверка: введено ли название файла
            //проверка: существование конфиг-файла
            if (!checkExistFileConfig()) {
                throw new RuntimeException(this.createErrorText("nFileName"));
            }

            // проверка: проверяем конфиг-файл на наличие ошибок
            if (this.flgVerifyFileConfig) {
                this.checkSyntaxFileConfig();
            }

            // читаем конфиг-файл и записываем строки в lineFileConf
            this.readLineFromFileConf();

            // анализ полученных параметров и значений
            this.analysisObtainedParametersValues();

            //удаляем информацию о предыдущей ошибке
            this.messageErr = "";

            return true;
        }
        catch (RuntimeException e){
            // записываем текст ошибки
            this.messageErr = this.createErrorText(e.getMessage()); //"Error: " + msg;
        }
        return false;
    }

    /**
     * Анализируем полученные строки из конфиг-файла.
     * Ищем строки с параметрами и значениями.
     * Сохраняем нужные параметры и значения.
     */
    private void analysisObtainedParametersValues () {
        String parameter;
        String value;

        // Проверяем. Нужны ли все параметры из конфига
        boolean flgAllPrms = mapRequiredParameters.isEmpty();

        //Очищаем предыдущий список полученых параметров
        clearListParameters();

        // анализ каждой строки
        for( String line : this.lineFileConf ){

            // делим строку на [параметр] = [значение]
            parameter = line.substring(0, line.indexOf(" ")).trim();
            value = line.substring(line.indexOf("=") + 1 ).trim();

            //нужны все параметры
            if(flgAllPrms){
                // создаем объект для параметра
                mapParametersValues.put(parameter, this.createValueObj(parameter));
                // сохраняем значение в объект
                objParameter(parameter).setValue(value);
            }

            // проверяем нужен ли нам найденный параметр
            if(mapRequiredParameters.containsKey(parameter)){

                mapParametersValues.put(parameter, mapRequiredParameters.get(parameter));

                // сохраняем значение в объект
                objParameter(parameter).setValue(value);
            }
        }

        // очищаем список, в котором хранились строки из конфига
        this.lineFileConf.clear();
    }

    /**
     * Построчное чтение файла с конфиг-файлом и сохранение в переменную.
     * Пропускаем: пустые строки, комментарии.
     * @throws RuntimeException - ConfigRd.java - ошибки при чтении файла
     */
    private void readLineFromFileConf () throws RuntimeException {

        try (FileReader fc = new FileReader(this.objFile)){
            BufferedReader reader = new BufferedReader(fc);
            String line;

            while ((line = reader.readLine()) != null) {
                // пустая строка
                if(line.isBlank()) continue;

                // удаляем пробелы в начале и конце строки
                line = line.trim();

                // пропускаем строки с комментариями в начале строки
                if(line.startsWith("#")) continue;

                // удаляем из строки комментарий
                if(line.indexOf("#") > 1){
                    line = line.substring(0, line.indexOf("#"));
                }

                //проверяем есть ли знак "="
                if(line.indexOf("=") < 1) continue;

                // добавляем строку для обработки
                this.lineFileConf.add(line);

            }
        }
        catch (Exception e){
            throw new RuntimeException (this.createErrorText("nReadingFile"));
        }
    }

    /**
     * Формируем список параметров, которые требуется прочитать из конфиг-файла.
     * @param listNameParameters [HashSet<String>] - список параметров
     */
    public void setListOfRequiredParameters (HashSet<String> listNameParameters){

        HashMap<String, HashMap<String,String>> nameParamsHashMap = new HashMap<>();

        for(String nameP : listNameParameters){
            HashMap<String,String> emptyHashMap = new HashMap<>();
            nameParamsHashMap.put(nameP, emptyHashMap);
        }

        this.setListOfRequiredParametersObjects(nameParamsHashMap);
    }

    /**
     * Для всех запрашиваемых параметров создаем объект ValueObj.
     * @param avParam [HashMap<String,HashMap<String,String>>] - список параметров с установленными свойствами
     */
    public void setListOfRequiredParametersObjects (HashMap<String,HashMap<String,String>> avParam){

        // очищаем список
        clearListRequiredParameters();

        for (Map.Entry<String,HashMap<String,String>> aP : avParam.entrySet()){

            // создаем объект для параметра
            ValueObj newVO = this.createValueObj(aP.getKey());

            // ПРОВЕРЯЕМ ОБЯЗАТЕЛЬНЫЕ АТРИБУТЫ КАЖДОГО ПАРАМЕТРА
            // ТИП ПАРАМЕТРА "type"
            // - если тип не указан, будет установлен тип string
            if( aP.getValue().containsKey("type") ){
                newVO.setTypeValue(aP.getValue().get("type"));
            }

            //значение по умолчанию
            if( aP.getValue().containsKey("default") ){
                newVO.setDefaultValue(aP.getValue().get("default"));
            }

            // МНОГОЗНАЧНОЕ ЗНАЧЕНИЕ
            // - если значение не указано, будет установлено true
            if( aP.getValue().containsKey("single") ){
                if( aP.getValue().get("single").equals("false") ){ newVO.setSingleValue(false);}
            }

            // добавляем объект ValueObj с атрибутами параметра
            mapRequiredParameters.put(aP.getKey(), newVO);
        }
    }

    /**
     * Очищаем предыдущий список запрашиваемых параметров.
     */
    public void clearListRequiredParameters () {
        mapRequiredParameters.clear();
    }

    /**
     * Очищаем предыдущий список полученых параметров.
     */
    public void clearListParameters () {
        mapParametersValues.clear();
    }

    /**
     * Возвращет абсолютный путь к конфиг-файлу.
     * @return String - абсолютный путь к файлу
     */
    public String getAbsolutePath () {
        if ( this.objFile != null ) {
            return this.objFile.getAbsolutePath();
        }
        return "";
    }

    /**
     * Возвращает путь к конфиг-файлу, назначенный перед сканированием.
     * @return String - путь к файлу
     */
    public String getFileConfig () {
        if ( this.objFile != null ) {
            return this.objFile.toString();
        }
        return "";
    }

    /**
     * Назначаем путь к конфиг-файлу перед сканированием. Сохраняем ссылку на файл и название файла.
     * @param fileConfig [String] - путь и название файла
     * @return boolean - [true] - файл существует
     */
    public boolean setFileConfig (String fileConfig) {
        try {
            File file = new File(fileConfig);

            if (file.isFile() && !file.isDirectory() && file.exists()) {
                this.objFile = file;
                this.fileConfig = fileConfig;
                return true;
            }
        } catch (Exception e) {
            // ОСТАВЛЯЕМ ПУСТЫМ.
            // если ошибка NullPointerException - тогда return false и программа продолжается
            // если добавить throw new RuntimeException() - программа останавливается
        }
        return false;
    }

    /**
     * Проверяем существование конфиг-файла.
     * @return boolean - [true] - файл существует
     */
    public boolean checkExistFileConfig () {
        return (this.objFile != null && this.objFile.exists());
    }

    /**
     * Проверяем конфиг-файл на наличие ошибок.
     * @return boolean - [true] - ошибок не найдено
     */
    private boolean checkSyntaxFileConfig() {

        // НУЖНА ПРОВЕРКА
        // !!! запрещено в значении знак "="
        //  (обязательные поля, синтаксис и т.п.)
        // превышен размер файла с конфигом
        // параметры без значений
        // значения без параметров

        return true;
    }

    /**
     * Возвращаем значение. Тип String
     * @param nameParameter [String] - название параметра
     * @throws IllegalArgumentException - ConfigRd.java - значение не найдено или некорректно
     * @return String - значение параметра
     */
    public String getParameterValue (String nameParameter) throws IllegalArgumentException {
        String exceptn;

        if(this.isExistParameter(nameParameter)) return objParameter(nameParameter).getValue();

        exceptn = this.createErrorText("nFoundIncorrect", nameParameter);
        if (this.flgReturnThrowsForValue) throw new IllegalArgumentException (exceptn);
        else System.out.println(exceptn);
        return null;
    }

    /**
     * Возвращаем значение. Тип Integer
     * @param nameParameter [String] - название параметра
     * @throws IllegalArgumentException - ConfigRd.java - значение не найдено или некорректно
     * @throws NumberFormatException - ConfigRd.java - неверный формат числа
     * @return Integer - значение параметра
     */
    public Integer getParameterValueInt (String nameParameter) throws IllegalArgumentException, NumberFormatException {

        String exceptn;

        if(this.isExistParameter(nameParameter) && objParameter(nameParameter).isTypeValue("int")){

            try{
                return Integer.decode(objParameter(nameParameter).getValue());
            }
            catch (NumberFormatException e) {
                exceptn = this.createErrorText("nIncorrectValue", nameParameter);
                if (this.flgReturnThrowsForValue) throw new NumberFormatException (exceptn);
                else System.out.println(exceptn);
                if (this.flgReturnDefaultValue) return Integer.decode(objParameter(nameParameter).getDefaultValue());
                return null;
            }
        }
        exceptn = this.createErrorText("nFoundIncorrect", nameParameter);
        if (this.flgReturnThrowsForValue) throw new IllegalArgumentException (exceptn);
        else System.out.println(exceptn);
        return null;
    }

    /**
     * Возвращаем значение. Тип Float
     * @param nameParameter [String] - название параметра
     * @throws IllegalArgumentException - ConfigRd.java - значение не найдено или некорректно
     * @throws NumberFormatException - ConfigRd.java - неверный формат числа
     * @return Float - значение параметра
     */
    public Float getParameterValueFloat (String nameParameter) throws IllegalArgumentException, NumberFormatException {

        String exceptn;

        if(isExistParameter(nameParameter) && objParameter(nameParameter).isTypeValue("float")) {
            try{
                return Float.parseFloat(objParameter(nameParameter).getValue());
            }
            catch (NumberFormatException e) {
                exceptn = this.createErrorText("nIncorrectValue", nameParameter);
                if (this.flgReturnThrowsForValue) throw new NumberFormatException (exceptn);
                else System.out.println(exceptn);
                if (this.flgReturnDefaultValue) return Float.parseFloat(objParameter(nameParameter).getDefaultValue());
            }
        }
        exceptn = this.createErrorText("nFoundIncorrect", nameParameter);
        if (this.flgReturnThrowsForValue) throw new IllegalArgumentException (exceptn);
        else System.out.println(exceptn);
        return null;
    }

    /**
     * Возвращаем список значений из параметра.
     * @param nameParameter [String] - название параметра
     * @return List<String> - список значений
     */
    public List<String> getParameterMultiValues (String nameParameter) {
        List<String> lstVl = new ArrayList<>();
        if(isExistParameter(nameParameter) && objParameter(nameParameter).isTypeValue("string")){
            String value = objParameter(nameParameter).getValue();
            String [] arrV = value.substring(value.indexOf('"') + 1, value.indexOf("\"", value.indexOf("\"") + 1 )).split(delimitingSymbol);
            lstVl.addAll(Arrays.asList(arrV));
        }
        return lstVl;
    }

    /**
     * Получаем список параметров, которые смогли найти в конфиг-файле (согласно нашего списка).
     * @return Set<String> - список полученных параметров
     */
    public Set<String> getListParameters (){
        return mapParametersValues.keySet();
    }

    /**
     * Проверяем было ли получено значение после чтения конфиг-файла.
     * @param nameParameter [String] - название параметра
     * @return boolean - [true] - параметр был прочитан в файле конфига
     */
    public boolean isExistParameter (String nameParameter) {
        return mapParametersValues.containsKey(nameParameter);
    }

    /**
     * Проверяем наличие ошибок при чтении конфиг-файла.
     * @return boolean - [true] - ошибок не найдено
     */
    public boolean getErrorStatus () {
        return !this.getErrorText().isEmpty();
    }

    /**
     * Получаем текст последней ошибки, которая произошла при чтении конфиг-файла.
     * @return String - текст ошибки
     */
    public String getErrorText () {
        return this.messageErr;
    }

    /**
     * Формируем текст ошибки.
     * @param indxErr [String] - текстовый идентификатор ошибки
     * @return String - текст ошибки
     */
    private String createErrorText (String indxErr) {
        return this.createErrorText(indxErr,"");
    }

    /**
     * Формируем текст ошибки.
     * @param indxErr [String] - текстовый идентификатор ошибки
     * @param namePrmtr [String] - название параметра
     * @return String - текст ошибки
     */
    private String createErrorText (String indxErr, String namePrmtr) {
        return switch (indxErr) {
            case "nFileName" -> "Error: File name not found.";
            case "nFileConfig" -> "Error: Configuration file not found.";
            case "nReadingFile" -> "Error: Reading the configuration file.";
            case "nIncorrectValue" -> String.format("Error: Parameter [%s] contains an incorrect value.", namePrmtr);
            case "nFoundIncorrect" ->
                    String.format("Error: Parameter [%s] was not found or has an incorrect type.", namePrmtr);
            default -> "Error: " + indxErr;
        };
    }

    /**
     * Возвращаем символ-разделитьль для многозначных значений.
     * @return String - символ-разделитель
     */
    public String getDelimitingSymbol () {
        return this.delimitingSymbol;
    }

    /**
     * Назначаем символ-разделитель для многозначных значений.
     * @param symbol [String] - символ-разделитель
     */
    public void setDelimitingSymbol (String symbol) {
        this.delimitingSymbol = symbol;
    }

    /**
     * Получаем значение, которое указывает нужно ли проверять синтаксис конфиг-файла на наличие ошибок.
     * @return boolean - [true] - нужна проверка файла
     */
    public boolean getFlgVerifyFileConfig () {
        return flgVerifyFileConfig;
    }

    /**
     * Устанавливаем значение, которое указывает нужно ли проверять синтаксис конфиг-файла на наличие ошибок.
     * @param flgVerifyFileConfig [boolean] -
     */
    public void setFlgVerifyFileConfig (boolean flgVerifyFileConfig) {
        this.flgVerifyFileConfig = flgVerifyFileConfig;
    }

    /**
     * Получаем значение, которое указывает нужно ли возвращать Exceptions при запросе несуществующего параметра
     * или при выводе значения.
     * @return boolean - [true] - нужно возвращать Exceptions
     */
    public boolean getFlgReturnThrowsForValue() {
        return flgReturnThrowsForValue;
    }

    /**
     * Устанавливаем значение, которое указывает нужно ли возвращать Exceptions при запросе несуществующего параметра
     * или при выводе значения.
     * @param flgReturnThrowsForValue [boolean] -
     */
    public void setFlgReturnThrowsForValue(boolean flgReturnThrowsForValue) {
        this.flgReturnThrowsForValue = flgReturnThrowsForValue;
    }

    /**
     * Нужно ли возвращать значение по умолчанию, если после чтения конфиг-файла при получении значения возникли ошибки.
     * @return boolean - [true] -
     */
    public boolean getFlgReturnDefaultValue() {
        return flgReturnDefaultValue;
    }

    /**
     * Устанавливаем значение, которое указывает нужно ли возвращать значение по умолчанию, если после чтения конфиг-файла
     * при получении значения возникли ошибки.
     * @param flgReturnDefaultValue [boolean] -
     */
    public void setFlgReturnDefaultValue(boolean flgReturnDefaultValue) {
        this.flgReturnDefaultValue = flgReturnDefaultValue;
    }

    /**
     * Создаем объект для хранения настроек параметра.
     * @param nameParameter [String] - название параметра
     * @return ValueObj - объект для параметра
     */
    private ValueObj createValueObj (String nameParameter){
        return new ValueObj(nameParameter);
    }

    /**
     * Возвращаем объект для параметра, который был найден в конфиг-файле.
     * @param nameParameter [String] - название параметра
     * @return ValueObj - объект параметра
     */
    private ValueObj objParameter (String nameParameter){
        return mapParametersValues.get(nameParameter);
    }

    // ВЛОЖЕННЫЙ КЛАСС
    // для хранения значения
    // все параметры это строки
    // все значения хранятся в строке
    private class ValueObj {

        private final String name;
        private String valueStr = "";
        private String typeValue = "string";
        private boolean singleValue = true;
        private String defaultValue = null;

        ValueObj(String name){
            this.name = name;
        }

        public boolean isTypeValue (String type){
            return this.getTypeValue().equals(type);
        }

        public boolean isSingleValue (){
            return this.singleValue;
        }

        public void setSingleValue (boolean singleValue){
            this.singleValue = singleValue;
        }

        public void setValue (String vl){
            this.valueStr = vl;
        }

        public String getValue (){
            return this.valueStr;
        }

        public void setTypeValue(String tp){
            this.typeValue = tp;
        }

        public String getTypeValue(){
            return this.typeValue;
        }

        public String getName (){
            return name;
        }

        public void setDefaultValue (String value){
            this.defaultValue = value;
        }

        public String getDefaultValue() {
            return Objects.requireNonNullElseGet(this.defaultValue, () -> switch (this.getTypeValue()) {
                case "int", "float" -> "0";
                default -> "";
            });
        }
    }

}
