<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="data4">
<data5>
  <xsl:apply-templates/>
  <data5_body></data5_body>
</data5>
</xsl:template>

<xsl:template match="data4_title">
<data5_title><xsl:apply-templates/></data5_title>
</xsl:template>

</xsl:stylesheet>

