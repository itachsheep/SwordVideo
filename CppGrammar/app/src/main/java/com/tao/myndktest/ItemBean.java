/**
 * @ClassName:
 * @Description:
 * @author taowei
 * @version V1.0
 * @Date
 */

package com.tao.myndktest;

class ItemBean {
   String title;
   Action action;

   public ItemBean(String title,Action action){
      this.title = title;
      this.action = action;
   }
}

interface Action {
   void onAction();
}
