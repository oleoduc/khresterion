<#macro renderCommentPosture>
     <#local commentProperty = instance.findProperty('Ontologie1_Analyse/Ontologie1_estComposeDe/Ontologie1_SyntheseDeLanalyse/Ontologie1_estComposeDe/Ontologie1_Proposition/Ontologie1_estComposeDe/Ontologie1_CommentaireSurLaProposition')>
    <#local comment = PropertyWrapper.getValue(commentProperty)>
    <fo:block>
        ${PropertyWrapper.getName(commentProperty)} ${utils.colon()}
    </fo:block>
    <@kbuilderUtils.multiLine_into_multiBlock comment />
</#macro>

<#macro renderCalkStopped>
    <#if (!kbuilderUtils.isCalkRunning())>
        <fo:table space-after="15pt">
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell width="5cm">
                        <fo:block padding-top="6pt" font-weight="bold">
                            <@kbuilderUtils.icon 'loopError'/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="justify">
                            <fo:inline font="8pt Helvetica" color="#BE0926">${utils.i18n('infinite.loop.dialog.report.message')}</fo:inline>                    
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </#if>
</#macro>


<#macro renderTitle title iconBaseName='' arg=''>
<fo:table space-after="10pt" border="solid 0.5pt black">
                <fo:table-column column-width="360pt"/>
                <fo:table-column column-width="180pt"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell border="solid 1pt black">
                            <fo:block padding-top="6pt" padding-bottom="6pt">
                                <fo:inline font="16pt Calibri"  font-weight="bold">${title}</fo:inline>                                
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="solid 1pt black">
                            <fo:block text-align="center">
                        <fo:inline color="black">${arg}</fo:inline>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
</#macro>

<#macro renderHeader>
<fo:block>
</fo:block>
</#macro>
