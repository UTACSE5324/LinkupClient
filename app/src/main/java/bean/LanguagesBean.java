package bean;

import java.util.List;

/**
 * Name: LanguagesBean
 * Description: Bean for the language list received
 * Structure will be like
 *           {
 *               "data":{
 *                   [{
 *                       "name":"Hindi"
 *                       "language":"hi"
 *                   },
 *                   {
 *                       "name":"English"
 *                       "language":"en"
 *                   }
 *                   ]
 *               }
 *           }
 * Created on 2016/10/2 0002.
 */

public class LanguagesBean {
     DataBean data;

     public DataBean getData() {
          return data;
     }

     public void setData(DataBean data) {
          this.data = data;
     }

     public class DataBean{
          List<LanguageBean> languages;

          public void setLanguages(List<LanguageBean> languages) {
               this.languages = languages;
          }

          public List<LanguageBean> getLanguages() {
               return languages;
          }
     }

     public class LanguageBean{
          String language;
          String name;

          public String getLanguage() {
               return language;
          }

          public void setLanguage(String language) {
               this.language = language;
          }

         public String getName() {
             return name;
         }

         public void setName(String name) {
             this.name = name;
         }
     }
}
