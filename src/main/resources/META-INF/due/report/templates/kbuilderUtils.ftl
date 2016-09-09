<#-- ======================= MACROS ===================================== -->

<#macro icon iconBaseName args...>
	<fo:external-graphic src="url('${iconManager.getUrl(iconBaseName, args).toString()}')"/>
</#macro>

<#macro multiLine_into_multiBlock text>
	<#list text.split("\n") as aSingleLine>
		<fo:block>
			${aSingleLine}
		</fo:block>
	</#list>
</#macro>

<#macro multiLine elements>



<#if elements?has_content>
<fo:block>
<fo:list-block>
    <#list elements as text>        
        <#list text.split("\n") as aSingleLine>
        <fo:list-item>
        <fo:list-item-label><fo:block></fo:block></fo:list-item-label>
          <fo:list-item-body start-indent="0.5in"><fo:block>${utils.getCircle()} ${aSingleLine}</fo:block></fo:list-item-body>
        </fo:list-item>
        </#list>         
    </#list>
    <fo:list-item><fo:list-item-label><fo:block></fo:block></fo:list-item-label>
    <fo:list-item-body><fo:block></fo:block></fo:list-item-body></fo:list-item>
    </fo:list-block>
    </fo:block>
 <#else>
    <fo:block></fo:block>
  </#if>

</#macro>

<#-- =======================   FUNCTIONS ================================ -->
<#function isCalkRunning>
	<#return session.getCalk().isRunning()>
</#function>

<#function i18n key>
	<#return i18nManager.getValue(key, [])>
</#function>

<#function i18nWithParameters key parameters>
	<#return i18nManager.getValue(key, parameters)>
</#function>
