<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="3.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:zp="http://ziphil.com/XSL">
  <xsl:output method="xml" indent="no"/>

  <xsl:param name="caption-font-family" select="'FreeSans, 源ノ角ゴシック'"/>
  <xsl:param name="caption-font-size" select="'20pt'"/>
  <xsl:param name="head-font-family" select="'FreeSans, 源ノ角ゴシック'"/>
  <xsl:param name="head-font-size" select="'10pt'"/>
  <xsl:param name="shaleia-font-family" select="'FreeSans, 源ノ角ゴシック'"/>
  <xsl:param name="shaleia-font-size" select="'9.5pt'"/>
  <xsl:param name="main-font-family" select="'Linux Libertine, 源ノ明朝'"/>
  <xsl:param name="main-font-size" select="'8pt'"/>
  <xsl:param name="title-font-size" select="'6pt'"/>
  <xsl:param name="color" select="'#333333'"/>
  <xsl:param name="title-color" select="'#333333'"/>
  <xsl:param name="light-color" select="'#DDDDDD'"/>
  <xsl:param name="leader-color" select="'#AAAAAA'"/>
  <xsl:param name="line-height" select="1.4"/>
  <xsl:param name="border-width" select="'0.2mm'"/>
  <xsl:param name="caption-border-width" select="'0.5mm'"/>
  <xsl:param name="inner-space" select="'0.5mm'"/>
  <xsl:param name="inner-margin" select="'1mm'"/>
  <xsl:param name="punctuation" select="', '"/>
  <xsl:param name="relation-marker" select="'cf:'"/>
  <xsl:param name="modifies" select="true()"/>

  <xsl:template match="/">
    <fo:root xml:lang="ja">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="body"
                               page-width="21.0cm"
                               page-height="29.7cm"
                               margin="10mm 15mm 10mm 15mm">
          <fo:region-body margin="13mm 5mm 13mm 5mm"
                          column-count="2"
                          column-gap="5mm"/>
          <fo:region-before extent="5mm" precedence="true"/>
          <fo:region-after extent="5mm" precedence="true"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <xsl:call-template name="bookmark"/>
      <fo:page-sequence master-reference="body">
        <fo:static-content flow-name="xsl-region-before">
          <fo:block-container height="5mm" display-align="after">
            <fo:block font-family="{$head-font-family}"
                      font-size="{$main-font-size}"
                      text-align-last="justify"
                      border-bottom="{$border-width} #000000 solid">
              <fo:inline padding="0mm 1mm 0mm 1mm">
                <fo:retrieve-marker retrieve-class-name="name"
                                    retrieve-position="first-including-carryover" 
                                    retrieve-boundary="page-sequence"/>
              </fo:inline>
              <fo:leader leader-pattern="space"/>
              <fo:inline padding="0mm 1mm 0mm 1mm">
                <fo:retrieve-marker retrieve-class-name="name"
                                    retrieve-position="last-starting-within-page" 
                                    retrieve-boundary="page-sequence"/>
              </fo:inline>
            </fo:block>
          </fo:block-container>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
          <fo:block-container height="5mm" display-align="before">
            <fo:block font-family="{$main-font-family}"
                      font-size="{$main-font-size}"
                      text-align="center">
              <fo:inline>
                <xsl:text>— </xsl:text>
                <fo:page-number/>
                <xsl:text> —</xsl:text>
              </fo:inline>
            </fo:block>
          </fo:block-container>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <xsl:apply-templates/>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <xsl:template name="bookmark">
    <fo:bookmark-tree>
      <xsl:if test="count(words/caption) > 0">
        <fo:bookmark internal-destination="caption-{words/caption[1]}"
                     starting-state="hide"> 
          <fo:bookmark-title>目次</fo:bookmark-title>
          <xsl:for-each select="words/caption">
            <fo:bookmark internal-destination="caption-{.}">
              <fo:bookmark-title>
                <xsl:value-of select="."/>
              </fo:bookmark-title>
            </fo:bookmark>
          </xsl:for-each>
        </fo:bookmark>
      </xsl:if>
    </fo:bookmark-tree>
  </xsl:template>

  <xsl:template match="words/caption">
    <fo:block space-before="3mm"
              space-before.minimum="2mm"
              space-before.maximum="5mm"
              space-after="3mm"
              space-after.minimum="2mm"
              space-after.maximum="5mm"
              text-align="center">
      <xsl:attribute name="id">
        <xsl:text>caption-</xsl:text>
        <xsl:value-of select="."/>
      </xsl:attribute>
      <fo:inline-container width="50%">
        <fo:block padding="{$caption-border-width}"
                  border="{$caption-border-width} {$color} solid">
          <fo:block font-family="{$caption-font-family}"
                    font-size="{$caption-font-size}"
                    line-height="1.8"
                    color="#FFFFFF"
                    background-color="{$color}"
                    text-align="center">
            <xsl:value-of select="."/>
          </fo:block>
        </fo:block>
      </fo:inline-container>
    </fo:block>
  </xsl:template>

  <xsl:template match="words/word">
    <fo:block space-before="3mm"
              space-before.minimum="2mm"
              space-before.maximum="5mm"
              space-after="3mm"
              space-after.minimum="2mm"
              space-after.maximum="5mm"
              border="{$border-width} {$color} solid">
      <fo:marker marker-class-name="name">
        <xsl:value-of select="name"/>
      </fo:marker>
      <xsl:call-template name="name"/>
      <xsl:call-template name="equivalents"/>
      <xsl:if test="count(contents/content) > 0">
        <xsl:call-template name="leader"/>
      </xsl:if>
      <xsl:if test="count(contents/content) > 0">
        <xsl:call-template name="contents"/>
      </xsl:if>
      <xsl:if test="count(synonyms/synonym) > 0">
        <xsl:call-template name="leader"/>
      </xsl:if>
      <xsl:if test="count(synonyms/synonym) > 0">
        <xsl:call-template name="synonyms"/>
      </xsl:if>
    </fo:block>
  </xsl:template>

  <xsl:template name="name">
    <fo:block color="#FFFFFF"
              background-color="{$color}"
              keep-with-next.within-column="always"
              keep-with-next.within-page="always">
      <fo:inline padding="0mm 2.5mm 0mm 1.5mm"
                 font-family="{$head-font-family}"
                 font-size="{$head-font-size}"
                 line-height="{$line-height}">
        <xsl:value-of select="name"/>
      </fo:inline>
      <fo:inline font-family="{$main-font-family}"
                 font-size="{$title-font-size}"
                 line-height="{$line-height}">
        <fo:inline padding="0.2mm 0.5mm 0.2mm 0.5mm"
                   color="{$title-color}"
                   background-color="{$light-color}"
                   alignment-baseline="central">
          <xsl:sequence select="zp:textify(sort)"/>
        </fo:inline>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <xsl:template name="equivalents">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="equivalents/equivalent">
        <fo:block font-family="{$main-font-family}"
                  font-size="{$main-font-size}"
                  line-height="{$line-height}">
          <fo:inline padding="0.2mm 0.5mm 0.2mm 0.5mm"
                     font-size="{$title-font-size}"
                     color="{$title-color}"
                     border="{$border-width} {$color} solid"
                     background-color="{$light-color}">
            <xsl:sequence select="zp:textify(category)"/>
          </fo:inline>
          <fo:inline>
            <xsl:text> </xsl:text>
          </fo:inline>
          <fo:inline>
            <xsl:sequence select="zp:textify(equivalent)"/>
          </fo:inline>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>

  <xsl:template name="contents">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="contents/content">
        <fo:block font-family="{$main-font-family}"
                  font-size="{$main-font-size}"
                  line-height="{$line-height}">
          <fo:block margin-left="-{$inner-margin}"
                    margin-bottom="0.2mm"
                    line-height="1"
                    keep-with-next.within-column="always"
                    keep-with-next.within-page="always">
            <fo:inline padding="0mm 3mm 0mm 0.8mm"
                       font-size="{$title-font-size}"
                       color="{$color}"
                       border-bottom="{$border-width} {$color} solid">
              <xsl:sequence select="zp:textify(title)"/>
            </fo:inline>
          </fo:block>
          <fo:block text-align="justify">
            <xsl:sequence select="zp:textify(content)"/>
          </fo:block>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>

  <xsl:template name="synonyms">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="synonyms/synonym">
        <fo:block font-family="{$main-font-family}"
                  font-size="{$main-font-size}"
                  line-height="{$line-height}">
          <fo:inline space-end="0.2mm"
                     font-size="{$title-font-size}"
                     color="{$color}">
            <xsl:value-of select="$relation-marker"/>
          </fo:inline>
          <fo:inline padding="0.2mm 0.5mm 0.2mm 0.5mm"
                     font-size="{$title-font-size}"
                     color="{$title-color}"
                     border="{$border-width} {$color} solid"
                     background-color="{$light-color}">
            <xsl:sequence select="zp:textify(synonym-type)"/>
          </fo:inline>
          <fo:inline>
            <xsl:text> </xsl:text>
          </fo:inline>
          <fo:inline>
            <xsl:sequence select="zp:textify(synonym)"/>
          </fo:inline>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>

  <xsl:template name="leader">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              border-bottom="{$border-width} {$leader-color} dotted"
              keep-with-previous.within-column="always"
              keep-with-previous.within-page="always">
    </fo:block>
  </xsl:template>

  <xsl:function name="zp:textify">
    <xsl:param name="text"/>
    <xsl:analyze-string select="$text" regex="\{{(.*?)\}}|\[(.*?)\]|/(.*?)/">
      <xsl:matching-substring>
        <xsl:choose>
          <xsl:when test="matches(., '\{.*?\}')">
            <fo:inline font-family="{$shaleia-font-family}"
                       font-size="{$shaleia-font-size}">
              <xsl:sequence select="zp:textify(regex-group(1))"/>
            </fo:inline>
          </xsl:when>
          <xsl:when test="matches(., '\[.*?\]')">
            <fo:inline font-family="{$shaleia-font-family}"
                       font-size="{$shaleia-font-size}">
              <xsl:sequence select="zp:textify(regex-group(2))"/>
            </fo:inline>
          </xsl:when>
          <xsl:when test="matches(., '/.*?/')">
            <fo:inline font-style="italic">
              <xsl:sequence select="zp:textify(regex-group(3))"/>
            </fo:inline>
          </xsl:when>
        </xsl:choose>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
        <xsl:choose>
          <xsl:when test="$modifies">
            <xsl:sequence select="zp:separate(replace(., '&amp;#x002F;', '/'))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="replace(., '&amp;#x002F;', '/')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:function>

  <xsl:function name="zp:separate">
    <xsl:param name="text"/>
    <xsl:analyze-string select="$text" regex="[&#x0020;-&#x25CA;]+">
      <xsl:matching-substring>
        <fo:inline>
          <xsl:value-of select="."/>
        </fo:inline>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
        <xsl:sequence select="zp:modify(.)"/>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:function>

  <xsl:function name="zp:modify">
    <xsl:param name="text"/>
    <xsl:analyze-string select="$text" regex="、|。|「|」">
      <xsl:matching-substring>
        <xsl:choose>
          <xsl:when test=". = '、'">
            <xsl:text>､ </xsl:text>
          </xsl:when>
          <xsl:when test=". = '。'">
            <xsl:text>｡ </xsl:text>
          </xsl:when>
          <xsl:when test=". = '「'">
            <xsl:text> ｢</xsl:text>
          </xsl:when>
          <xsl:when test=". = '」'">
            <xsl:text>｣ </xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
        <xsl:value-of select="."/>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:function>

</xsl:stylesheet>