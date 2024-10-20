package com.example.hello;

public interface Button_Listener
{
    void Changes_Click();   //   点击改变后触发的函数
    void History_Click();   //   点击历史后触发的函数
    void Commit_Click(String VerSionName);    //   点击提交后触发的函数
    void Branch_Change(String newBranch);   //   点击切换分支后触发的函数
    void Changes_List_Selected(String selectedValue);  // 选中changes列表的触发函数
    void History_List_Selected(String selectedValue);  //选中History列表的触发函数
    void History_Change_Selected(String Selected_History_Name,String Selected_File_Name);  //选中History下的changes文件触发函数
    void Merge_Click(String Be_Merged_Name,String Merge_Name);   //点击合并后触发的函数
    void Branch_Build(String New_Branch_Name);  //点击新建分支
}
