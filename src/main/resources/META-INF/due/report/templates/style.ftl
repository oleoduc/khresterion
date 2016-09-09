<#function color_grey>
	<#return 'color="${grey()}"'>
</#function>

<#function grey>
	<#return 'rgb(74,74,74)'>
</#function>


<#function page>
	<#return 'font="10pt Calibri" color="black" line-height="14pt"'>
</#function>

<#function title1>
	<#return 'space-after="3pt"
			  ${font_title1()} 
			  padding-top="5"
              padding-bottom="5"'>
</#function>


<#function font_title1>
	<#return 'color="rgb(0,0,0)" 
			  font-weight="bold"
			  font-size="12pt"'>

</#function>


<#function title2>
	<#return 'font-weight="bold"  
			  color="rgb(0,0,0)" 
			  font-size="10pt" 
			  space-before="10pt" 
			  space-after="3pt"'>
</#function>


<#function therapeuticChoiceTableRow1>
	<#return '${tableBackground()}
			  height="24pt"
			  color="black" 
			  font-weight="bold"
			  font-size="10pt"'>
</#function>

<#function therapeuticChoiceTableRow2>
	<#return '${tableBackgroundAlt()}
			  height="24pt"
			  color="black" 
			  font-weight="bold"  
			  font-size="10pt"'>
</#function>


<#function medicalActLabel>
	<#return 'color="black" font-weight="bold" space-after="2pt"'>
</#function>


<#function propertyValue>
	<#return 'color="black"'>
</#function>

<#function stadification>
	<#return 'color="black"
			  font-weight="bold"
			  text-align="center"
			  ${tableBackground()}
			  ${tableBorder()}
			  padding-top="5"
			  padding-bottom="5"'>
</#function>

<#function tableBorder>
	<#return 'border-style="solid" border-color="rgb(200,200,200)"'>	
</#function>

<#function tableBackground>
	<#return 'background-color="rgb(240,240,240)"'>	
</#function>

<#function tableBackgroundAlt>
    <#return 'background-color="rgb(195,195,195)"'> 
</#function>

<#function incoherent>
    <#return 'color="rgb(193,39,45)"'>
</#function>

<#function uncomplete>
    <#return 'color="rgb(0,113,188)"'>
</#function>

<#function sinstraliteRow>
<#return 'background-color="rgb(255,255,0)"
              height="24pt"
              color="black" 
              font-weight="bold"  
              font-size="10pt"'>
</#function>