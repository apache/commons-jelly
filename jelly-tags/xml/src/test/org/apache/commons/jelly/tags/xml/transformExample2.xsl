<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="data1">
<data2>
  <xsl:apply-templates/>
  <data2_body></data2_body>
</data2>
</xsl:template>

<xsl:template match="data1_title">
<data2_title><xsl:apply-templates/></data2_title>
</xsl:template>

</xsl:stylesheet>

