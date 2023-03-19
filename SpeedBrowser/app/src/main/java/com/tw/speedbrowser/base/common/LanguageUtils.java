package com.tw.speedbrowser.base.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tw.speedbrowser.R;
import com.tw.speedbrowser.base.utils.LogUtils;
import com.tw.speedbrowser.base.utils.SPUtils;

import java.util.Locale;


// https://www.cnblogs.com/plokmju/p/android_rtl.html 适配UI从右向左
public class LanguageUtils {
    private static final String TAG = "LanguageUtils";
    private static LanguageUtils instance;
    private Context mContext;
    public static final String SAVE_LANGUAGE = "save_language";

    public static void init(Context mContext) {
        synchronized (LanguageUtils.class) {
            if (instance == null) {
                instance = new LanguageUtils(mContext);
            }
        }
    }

    public static LanguageUtils getInstance() {
        synchronized (LanguageUtils.class) {
            if (instance == null) {
                instance = new LanguageUtils();
            }
        }
        return instance;
    }


    private LanguageUtils() {

    }

    private LanguageUtils(Context context) {
        this.mContext = context;
    }


    private String getLanguage(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    /**
     * 设置语言
     */
    public void setConfiguration(Context context) {
        if (context == null) {
            LogUtils.INSTANCE.e(TAG, "No context, MultiLanguageUtil will not work!");
            return;
        }
        mContext = context.getApplicationContext();
        Context appContext = context.getApplicationContext();

        //#1294242 低于6.0的版本不需要初始化
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            LogUtils.INSTANCE.e(TAG, "os version < 6.0 Unsupported version");
            return;
        }

        Locale sysLocale = getSysLocale();
        LogUtils.INSTANCE.d(TAG, "getLanguage sysLocale: " + getLanguage(sysLocale));
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(sysLocale);

        context.createConfigurationContext(configuration);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);//语言更换生效的代码!
    }


    /**
     * 获取国家码
     */
    public static String getCountryZipCode(Context context, String localStr) {
        String CountryZipCode = "";
//        Logger.e("当前语言 -------> " + localStr);
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (localStr.contains(g[1].trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        if (localStr.contains("VI")) {
            return "84";
        }
        return CountryZipCode;
    }


    /**
     * 如果不是英文、简体中文、繁体中文，默认返回繁体中文
     *
     * @return
     */
    public boolean hasTagInit = false;


    public Locale getLanguageLocale(Context context) {
        mContext = context;
        int languageType = getLanguageType();
        Locale sysLocale = getSysLocale();
//        String sysLocaleStr = sysLocale.toLanguageTag().toUpperCase();
        LogUtils.INSTANCE.d(TAG,"getLanguageLocale sysLocale: " + sysLocale + ", languageType: " + languageType);

        if (languageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
            if (sysLocale.toLanguageTag().toLowerCase().contains("tw")) {
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_CHINESE_TRADITIONAL);
                mCurLangType = LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
                return Locale.TAIWAN;
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("vi")) {
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_VI_RVN);
                mCurLangType = LanguageType.LANGUAGE_VI_RVN;
                return new Locale("vi", "VN");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("ko")) {
                mCurLangType = LanguageType.LANGUAGE_EN;
                //if (hasInit) {
                //    return Locale.KOREA;
                //} else {
                //    // 3.1.0新增修改
                //    if (UserLoginType.isLogin() && UserLoginType.showKo()) {
                //        return Locale.KOREA;
                //    }
                // 3.29.0  韩语下架
                return new Locale("en", "001");
                //}
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("ru")) {
                mCurLangType = LanguageType.LANGUAGE_RU_RRU;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_RU_RRU);
                return new Locale("ru", "RU");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("ja")) {
                mCurLangType = LanguageType.LANGUAGE_JA_RJP;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_JA_RJP);
                return Locale.JAPAN;
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("es")) {
                mCurLangType = LanguageType.LANGUAGE_ES;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_ES);
                return new Locale("es", "ES");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("id")) {
                mCurLangType = LanguageType.LANGUAGE_ID;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_ID);
                return new Locale("id", "ID");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("tr")) {
                mCurLangType = LanguageType.LANGUAGE_TR;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_TR);
                return new Locale("tr", "TR");
            }
            //else if (sysLocale.toLanguageTag().toLowerCase().contains("es")) {
            //    mCurLangType = LanguageType.LANGUAGE_ES;
            //    CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_ES);
            //    return new Locale("es", "ES");
            //}
            // 荷兰语
            else if (sysLocale.toLanguageTag().toLowerCase().contains("nl")) {
                mCurLangType = LanguageType.LANGUAGE_NL;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_NL);
                return new Locale("nl", "NL");
            }
            // 葡萄牙
            else if (sysLocale.toLanguageTag().toLowerCase().contains("pt")) {
                mCurLangType = LanguageType.LANGUAGE_PT;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_PT);
                return new Locale("pt", "PT");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("de")) {
                mCurLangType = LanguageType.LANGUAGE_DE;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_DE);
                return Locale.GERMANY;
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("fa")) {
                mCurLangType = LanguageType.LANGUAGE_FA;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_FA);
                return new Locale("fa", "IR");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("th")) {
                mCurLangType = LanguageType.LANGUAGE_TH;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_TH);
                return new Locale("th", "TH");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("ar")) {
                mCurLangType = LanguageType.LANGUAGE_AR;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_AR);
                return new Locale("ar", "AR");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("fr")) {
                mCurLangType = LanguageType.LANGUAGE_FR;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_FR);
                return new Locale("fr", "FR");
            } else if (sysLocale.toLanguageTag().toLowerCase().contains("it")) {
                mCurLangType = LanguageType.LANGUAGE_IT;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_IT);
                return new Locale("it", "IT");
            } else {
                mCurLangType = LanguageType.LANGUAGE_EN;
                CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_EN);
                return new Locale("en", "001");
            }
        } else if (languageType == LanguageType.LANGUAGE_EN) {
            return new Locale("en", "001");
        } else if (languageType == LanguageType.LANGUAGE_VI_RVN) {
            return new Locale("vi", "VN");
        } else if (languageType == LanguageType.LANGUAGE_KO_RKR) {
            // 2.51.0新增
            //if (hasInit) {
            //    return Locale.KOREA;
            //} else {
            //    if (UserLoginType.isLogin() && UserLoginType.showKo()) {
            //        return Locale.KOREA;
            //    }
            return new Locale("ko", "KR");
        } else if (languageType == LanguageType.LANGUAGE_JA_RJP) {
            return Locale.JAPAN;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return Locale.TAIWAN;
        } else if (languageType == LanguageType.LANGUAGE_RU_RRU) {
            return new Locale("ru", "RU");
        } else if (languageType == LanguageType.LANGUAGE_ES) {
            return new Locale("es", "ES");
        } else if (languageType == LanguageType.LANGUAGE_ID) {
            return new Locale("id", "ID");
        } else if (languageType == LanguageType.LANGUAGE_TR) {
            return new Locale("tr", "TR");
        }
        // 荷兰语言
        else if (languageType == LanguageType.LANGUAGE_NL) {
            return new Locale("nl", "NL");
        }
        // 葡萄牙语
        else if (languageType == LanguageType.LANGUAGE_PT) {
            //为了支持crowdin 热更新,crowdin平台上用的是 pt-BR
            return new Locale("pt", "BR");
        } // 德语
        else if (languageType == LanguageType.LANGUAGE_DE) {
            return Locale.GERMANY;
        } // 波斯语
        else if (languageType == LanguageType.LANGUAGE_FA) {
            return new Locale("fa", "IR");
        }
        // 泰语语
        else if (languageType == LanguageType.LANGUAGE_TH) {
            return new Locale("th", "TH");
        }// 阿拉伯语
        else if (languageType == LanguageType.LANGUAGE_AR) {
            return new Locale("ar", "AR");
        } // 法语
        else if (languageType == LanguageType.LANGUAGE_FR) {
            return new Locale("fr", "FR");
        }  // 意大利语
        else if (languageType == LanguageType.LANGUAGE_IT) {
            return new Locale("it", "IT");
        } else {
            //为了支持crowdin 热更新,crowdin平台上用的是 en-001
            return new Locale("en", "001");
        }
    }


    //以上获取方式需要特殊处理一下
    public Locale getSysLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }




    public boolean isChineseType() {
        return getLanguageType() == LanguageType.LANGUAGE_CHINESE_TRADITIONAL
                || getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
    }

    public boolean isVinType() {
        return getLanguageType() == LanguageType.LANGUAGE_VI_RVN;
    }

    public boolean isEnglishType() {
        return getLanguageType() == LanguageType.LANGUAGE_EN;
    }

    public boolean isNlType() {
        return getLanguageType() == LanguageType.LANGUAGE_NL;
    }

    public boolean isPtType() {
        return getLanguageType() == LanguageType.LANGUAGE_PT;
    }

    public boolean isJpType() {
        return getLanguageType() == LanguageType.LANGUAGE_JA_RJP;
    }

    public boolean isRuType() {
        return getLanguageType() == LanguageType.LANGUAGE_RU_RRU;
    }

    public boolean isKoType() {
        return getLanguageType() == LanguageType.LANGUAGE_KO_RKR;
    }

    public boolean isEsType() {
        return getLanguageType() == LanguageType.LANGUAGE_ES;
    }

    public boolean isIdType() {
        return getLanguageType() == LanguageType.LANGUAGE_ID;
    }

    public boolean isTrType() {
        return getLanguageType() == LanguageType.LANGUAGE_TR;
    }

    public boolean isDeType() {
        return getLanguageType() == LanguageType.LANGUAGE_DE;
    }

    public boolean isFaType() {
        return getLanguageType() == LanguageType.LANGUAGE_FA;
    }


    public boolean isThType() {
        return getLanguageType() == LanguageType.LANGUAGE_TH;
    }

    public boolean isARType() {
        return getLanguageType() == LanguageType.LANGUAGE_AR;
    }


    public boolean isFRType() {
        return getLanguageType() == LanguageType.LANGUAGE_FR;
    }



    public boolean isITType() {
        return getLanguageType() == LanguageType.LANGUAGE_IT;
    }


    public boolean isTraditionalType() {
        return getLanguageType() == LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
    }

    public boolean isSimpleChineseType() {
        return getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
    }

    // 本机是否是韩语
    public boolean localIsKo() {
        String langStr = SPUtils.getInstance().getString("sys_lang_str");
        if (!TextUtils.isEmpty(langStr) && langStr.contains("ko")) {
            return true;
        }
        return false;
    }

    private int mCurLangType = LanguageType.LANGUAGE_FOLLOW_SYSTEM;

    /**
     * 获取到用户保存的语言类型
     *
     * @return
     */
    public int getLanguageType() {
        mCurLangType = LanguageType.LANGUAGE_FOLLOW_SYSTEM;
        return mCurLangType;
    }

    public void updateLanguageType(int langType) {
        CommSharedUtil.getInstance(mContext).putInt(LanguageUtils.SAVE_LANGUAGE, langType);
    }

    public String getLanguageTip(int lanType) {
        String language = "";
        if (lanType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            language = LanguageType.TRANDITIONAL_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_EN) {
            language = LanguageType.ENGLISH_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_JA_RJP) {
            language = LanguageType.JA_RJP_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_KO_RKR) {
            // 3.1.0新增
            //if (UserLoginType.showKo() && UserLoginType.isLogin()) {
            //    language = LanguageType.KO_RKR_TYEP;
            //} else {
            // 3.29.0  下架韩语
            language = LanguageType.ENGLISH_TYPE;
            //}
        } else if (lanType == LanguageType.LANGUAGE_VI_RVN) {
            language = LanguageType.VI_RVN_TYEP;
        } else if (lanType == LanguageType.LANGUAGE_RU_RRU) {
            language = LanguageType.RU_ERU_TYEP;
        } else if (lanType == LanguageType.LANGUAGE_ES) {
            language = LanguageType.ES_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_ID) {
            language = LanguageType.ID_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_TR) {
            language = LanguageType.TR_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_NL) {
            language = LanguageType.NL_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_PT) {
            language = LanguageType.PT_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_DE) {
            language = LanguageType.DE_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_FA) {
            language = LanguageType.FA_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_TH) {
            language = LanguageType.TH_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_AR) {
            language = LanguageType.AR_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_FR) {
            language = LanguageType.FR_TYPE;
        } else if (lanType == LanguageType.LANGUAGE_IT) {
            language = LanguageType.IT_TYPE;
        } else {
            // 默认走英文
            language = LanguageType.ENGLISH_TYPE;
        }
        return language;
    }

    public String getCurrentLanguageTip() {
        String language = "";
        int languageType = LanguageUtils.getInstance().getLanguageType();

        if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            language = LanguageType.TRANDITIONAL_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_EN) {
            language = LanguageType.ENGLISH_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_JA_RJP) {
            language = LanguageType.JA_RJP_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_KO_RKR) {
            //language = LanguageType.KO_RKR_TYEP;
            // 3.29.0 下架韩语
            language = LanguageType.ENGLISH_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_VI_RVN) {
            language = LanguageType.VI_RVN_TYEP;
        } else if (languageType == LanguageType.LANGUAGE_RU_RRU) {
            language = LanguageType.RU_ERU_TYEP;
        } else if (languageType == LanguageType.LANGUAGE_ES) {
            language = LanguageType.ES_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_ID) {
            language = LanguageType.ID_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_TR) {
            language = LanguageType.TR_TYPE;
        }
        // 荷兰语
        else if (languageType == LanguageType.LANGUAGE_NL) {
            language = LanguageType.NL_TYPE;
        }
        // 葡萄牙
        else if (languageType == LanguageType.LANGUAGE_PT) {
            language = LanguageType.PT_TYPE;
        }// 德语
        else if (languageType == LanguageType.LANGUAGE_DE) {
            language = LanguageType.DE_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_FA) {//波斯语
            language = LanguageType.FA_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_TH) {//泰语
            language = LanguageType.TH_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_AR) {//阿拉伯语
            language = LanguageType.AR_TYPE;
        } else if (languageType == LanguageType.LANGUAGE_FR) {//法语
            language = LanguageType.FR_TYPE;
        }else if (languageType == LanguageType.LANGUAGE_IT) {//意大利语
            language = LanguageType.IT_TYPE;
        } else {
            // 默认走英文
            language = LanguageType.ENGLISH_TYPE;
        }
        return language;
    }


    public String getWebLang() {
        if (isSimpleChineseType()) {
            return "zh-cn";
        }
        if (isEnglishType()) {
            return "en-us";
        }
        if (isTraditionalType()) {
            return "zh-tw";
        }
        return LanguageUtils.getInstance().getCurrentLanguageTip().toLowerCase();
    }





}