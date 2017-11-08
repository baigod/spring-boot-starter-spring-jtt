package me.douboo.springboot.spring.jtt.utils;


public class CamelNameUtils {
	
	/**
	 * convert camel name to underscore name
	 * @return
	 */
	public static String camel2underscore(String camelName){
		//鍏堟妸绗竴涓瓧姣嶅ぇ鍐�
		camelName = capitalize(camelName);
		
		String regex = "([A-Z][a-z0-9]+)";
		String replacement = "$1_";

		String underscoreName = camelName.replaceAll(regex, replacement);
		//output: Pur_Order_Id_ 鎺ヤ笅鏉ユ妸鏈�鍚庝竴涓猒鍘绘帀锛岀劧鍚庡叏閮ㄦ敼灏忓啓
		
		underscoreName = underscoreName.toLowerCase().substring(0, underscoreName.length()-1);
		
		return underscoreName;
	}
	
	/**
	 * convert underscore name to camel name
	 * @param underscoreName
	 * @return
	 */
	public static String underscore2camel(String underscoreName){
		String[] sections = underscoreName.split("_");
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<sections.length;i++){
			String s = sections[i];
			if(i==0){
				sb.append(s);
			}else{
				sb.append(capitalize(s));
			}
		}
		return sb.toString();
	}
	
	/**
	 * capitalize the first character
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }
}
