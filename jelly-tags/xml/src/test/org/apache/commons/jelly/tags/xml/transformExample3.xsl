<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="data2">
<data3>
  <xsl:apply-templates/>
  <data3_body></data3_body>
</data3>
</xsl:template>

<xsl:template match="data2_title">
<data3_title><xsl:apply-templates/></data3_title>
</xsl:template>

</xsl:stylesheet>

