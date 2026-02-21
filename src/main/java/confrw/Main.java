package confrw;

import java.util.HashMap;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        System.out.println("[method] main");//[DEBAG]

        System.out.println("--------------------");//[DEBAG]
        System.out.println("---- objCfg01");//[DEBAG]
        ConfigRead objCfg01 = new ConfigRead();

        objCfg01.setDelimitingSymbol(" ");
        objCfg01.setFlgVerifyFileConfig(false);
        objCfg01.setFlgReturnThrowsForValue(false);

        String flName1 = "src\\main\\resources\\conf.cnf";

        if(objCfg01.setFileConfig(flName1)) System.out.println("        file exist = [" + objCfg01.getFileConfig() + "]");//[DEBAG]
        else System.out.println("        file not exist [" + flName1 + "]");//[DEBAG]

        System.out.println("        getDelimitingSymbol = " + objCfg01.getDelimitingSymbol());//[DEBAG]
        System.out.println("        getFileConfig = " + objCfg01.getFileConfig());//[DEBAG]
        System.out.println("        getAbsolutePath = " + objCfg01.getAbsolutePath());//[DEBAG]
        System.out.println("        getFlgVerifyFileConfig() = " + objCfg01.getFlgVerifyFileConfig());//[DEBAG]
        System.out.println("        getFlgReturnThrowsForValue() = " + objCfg01.getFlgReturnThrowsForValue());//[DEBAG]
        System.out.println("        getFlgReturnDefaultValue() = " + objCfg01.getFlgReturnDefaultValue());//[DEBAG]


        // перечисляем какие параметры нам нужны
        HashSet<String> listParams01 = new HashSet<>();
        listParams01.add("vlSingleInt");
        listParams01.add("vlSingleInt2");
        listParams01.add("vlSingleInt2");//дублируем для проверки
        listParams01.add("vlStrConst2");
        listParams01.add("vlMultipleList");
        listParams01.add("vlSingleStringMultipleSpaces2");

        objCfg01.setListOfRequiredParameters(listParams01);

        //читаем конфиг-файл
        if ( objCfg01.scanConfigFile() ) {
            System.out.println("        getErrorStatus = " + objCfg01.getErrorStatus());//[DEBAG]
            System.out.println("        getErrorText = " + objCfg01.getErrorText());//[DEBAG]
        }
        else {
            System.out.println("        getErrorStatus = " + objCfg01.getErrorStatus());//[DEBAG]
            System.out.println("        getErrorText = " + objCfg01.getErrorText());//[DEBAG]
        }

        System.out.println("        getListParameters = " + objCfg01.getListParameters());//[DEBAG]

        System.out.println("----- TEST 01 -----");//[DEBAG]

        System.out.printf("[vlSingleInt] = %s \n", objCfg01.getParameterValue("vlSingleInt"));//[DEBAG]
        System.out.printf("[vlSingleStringMultipleSpaces2] = %s \n", objCfg01.getParameterValue("vlSingleStringMultipleSpaces2"));//[DEBAG]
        System.out.printf("[vlSingleStringMultipleSpaces2] = %s \n", objCfg01.getParameterMultiValues("vlSingleStringMultipleSpaces2"));//[DEBAG]
        System.out.printf("[vlSingleInt2] = %s <== this string \n", objCfg01.getParameterValue("vlSingleInt2"));//[DEBAG]



        System.out.println("--------------------");//[DEBAG]
        System.out.println("---- objCfg02");//[DEBAG]

        HashMap<String,HashMap<String,String>> parameters = new HashMap<>();
        HashMap<String,String> mapPrmVal01 = new HashMap<>();
        mapPrmVal01.put("type","int"); //
        mapPrmVal01.put("default","10");
        mapPrmVal01.put("single","true");
        parameters.put("vlSingleInt2", mapPrmVal01);

        HashMap<String,String> mapPrmVal02 = new HashMap<>();
        mapPrmVal02.put("type","float");
        mapPrmVal02.put("default","5.0");
        parameters.put("vlSingleFloat2", mapPrmVal02);

        String flName02 = "src\\main\\resources\\conf.cnf";
        ConfigRead objCfg02 = new ConfigRead();
        objCfg02.setListOfRequiredParametersObjects(parameters);
        objCfg02.setFlgReturnThrowsForValue(false);
        objCfg02.setFlgReturnDefaultValue(true);

        if (objCfg02.setFileConfig(flName02)){
            System.out.println("getErrorStatus = " + objCfg02.getErrorStatus());//[DEBAG]
        }
        else {
            System.out.println("getErrorStatus = " + objCfg02.getErrorStatus());//[DEBAG]
            System.out.println("getErrorText = " + objCfg02.getErrorText());//[DEBAG]
        }

        if (objCfg02.scanConfigFile()) {
            System.out.println("        getErrorStatus = " + objCfg02.getErrorStatus());//[DEBAG]
            System.out.println("        getListParameters = " + objCfg02.getListParameters());//[DEBAG]
        }
        else {
            System.out.println("        getErrorStatus = " + objCfg02.getErrorStatus());//[DEBAG]
            System.out.println("        getErrorText = " + objCfg02.getErrorText());//[DEBAG]
        }

        System.out.println("----- TEST 02 -----");//[DEBAG]
        System.out.printf("[vlSingleInt] = %s <== not required \n", objCfg02.getParameterValue("vlSingleInt"));//[DEBAG]
        System.out.printf("[vlSingleInt2] = %s <== this string \n", objCfg02.getParameterValue("vlSingleInt2"));//[DEBAG]
        System.out.printf("[vlSingleInt25] = %s <== not required \n", objCfg02.getParameterValue("vlSingleInt25"));//[DEBAG]
        System.out.printf("[vlSingleFloat2] = %s <== default value \n", objCfg02.getParameterValueFloat("vlSingleFloat2"));//[DEBAG]

        try {
            System.out.printf("[vlSingleInt2] = %s <== default value \n", objCfg02.getParameterValueInt("vlSingleInt2"));//[DEBAG]
        }
        catch(RuntimeException e){
            System.out.println("Error");//[DEBAG]
        }

        System.out.println("--------------------");//[DEBAG]
        System.out.println("---- END");//[DEBAG]
    }
}
