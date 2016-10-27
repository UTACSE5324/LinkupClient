package bean;

import java.util.List;

/**
 * Name: TranslateBean
 * Description: Bean for the translation result list received
 * Structure will be like
 *           {
 *               "data":{
 *                   [{
 *                       "translatedText":""
 *                   }
 *                   ]
 *               }
 *           }
 * Created on 2016/10/2 0002.
 */

public class TranslateBean {
    Data data;

    public Data getData(){
        return data;
    }

    public void setData(Data data){
        this.data = data;
    }


    public class Data{
        List<translation> translations;

        public class translation{
            public String getTranslatedText() {
                return translatedText;
            }

            public void setTranslatedText(String translatedText) {
                this.translatedText = translatedText;
            }

            String translatedText;
        }

        public List<translation> getTranslations(){
            return translations;
        }

        public void setTranslations(List<translation> translations){
            this.translations = translations;
        }
    }
}
