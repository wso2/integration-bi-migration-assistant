function activityExtension(Context cx) returns error? {
    
// Failed to codegen activity due to Java Invoke of instance method com.example.text.Formatter.format is not supported

    
// <tibex:activityExtension xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" inputVariable="FormatCode-input" name="FormatCode" outputVariable="FormatCode" tibex:xpdlId="e41a8c6d-0f25-4b93-9d7e-3c5b2a1f8064">
//     <bpws:targets>
//         <bpws:target linkName="StartToFormatCode"/>
//     </bpws:targets>
//     <bpws:sources>
//         <bpws:source linkName="FormatCodeToEnd"/>
//     </bpws:sources>
//     <tibex:inputBindings>
//         <tibex:inputBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#10;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns=&quot;www.tibco.com/plugin/java/JavaInvokeActivityInput+e41a8c6d-0f25-4b93-9d7e-3c5b2a1f8064+JavaInvokeActivityInput&quot; version=&quot;2.0&quot;&gt;&lt;xsl:param name=&quot;Start&quot;/&gt;&lt;xsl:template name=&quot;FormatCode-input&quot; match=&quot;/&quot;&gt;&lt;tns:JavaInvokeActivityInput&gt;&lt;MethodParameters&gt;&lt;text&gt;&lt;xsl:value-of select=&quot;$Start/code&quot;/&gt;&lt;/text&gt;&lt;/MethodParameters&gt;&lt;/tns:JavaInvokeActivityInput&gt;&lt;/xsl:template&gt;&lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
//     </tibex:inputBindings>
//     <tibex:config>
//         <bwext:BWActivity xmlns:activityconfig="http://tns.tibco.com/bw/model/activityconfig" xmlns:bwext="http://tns.tibco.com/bw/model/core/bwext" xmlns:java="http://ns.tibco.com/bw/palette/java" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" activityTypeID="bw.java.javamethod">
//             <activityConfig>
//                 <properties name="config" xsi:type="activityconfig:EMFProperty">
//                     <type href="http://ns.tibco.com/bw/palette/java#//JavaMethod"/>
//                     <value className="com.example.text.Formatter" isNew="true" isNewActivity="true" isStaticMethod="false" methodName="format" methodReturn="string" xsi:type="java:JavaMethod">
//                         <exceptionTypes>java.lang.Exception</exceptionTypes>
//                         <methodParameter paramName="text" paramType="string"/>
//                     </value>
//                 </properties>
//             </activityConfig>
//         </bwext:BWActivity>
//     </tibex:config>
// </tibex:activityExtension>

}
