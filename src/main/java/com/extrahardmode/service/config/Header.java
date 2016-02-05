package com.extrahardmode.service.config;


import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Quick Wrapper class for a header in a yaml file
 */
public class Header
{
    private String mHeading;
    private final List<String> mLines = new LinkedList<>();
    private final int mLineSize = 100;


    public void addLines(Collection<String> lines)
    {
        mLines.addAll(lines.stream().map(line -> "# " + line + StringUtils.repeat(" ", mLineSize - 2 - line.length() - 1) + "#%n").collect(Collectors.toList()));
    }


    public void addLines(String... lines)
    {
        addLines(Arrays.asList(lines));
    }


    public void setHeading(String heading)
    {
        this.mHeading = heading;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        //top box line
        sb.append(StringUtils.repeat("#", mLineSize)).append("%n");
        //centered heading
        sb.append('#').append(StringUtils.repeat(" ", mLineSize / 2 - mHeading.length() / 2 - 1)).append(mHeading)
                .append(StringUtils.repeat(" ", 100 / 2 - mHeading.length() / 2 - 1 - mHeading.length() % 2)).append("#%n");
        //body
        mLines.forEach(sb::append);
        //bottom box line
        sb.append(StringUtils.repeat("#", mLineSize)).append("%n");
        return sb.toString();
    }
}
