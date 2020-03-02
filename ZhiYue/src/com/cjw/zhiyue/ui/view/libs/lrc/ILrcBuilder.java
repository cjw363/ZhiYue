package com.cjw.zhiyue.ui.view.libs.lrc;


import java.util.List;

import com.cjw.zhiyue.ui.view.libs.lrc.impl.LrcRow;

/**
 * 解析歌词，得到LrcRow的集合
 */
public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
