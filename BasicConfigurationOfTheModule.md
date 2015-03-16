<h1>The basic configuration of the module<h1>

<h1>Introduction</h1>

A few number of parameters can be fixed to configure the module, in file  <i>/system/modules/com.eurelis.opencms.workflows/conf/module_config.txt</i>.<br>
<br>
<br>
<b>Note :</b> In most case, you don't have to configure this file, except in you change the module name, or the folder where will be stored the worklows descriptions (property <i>WORKFLOWLIST_CONFIGFILE_FOLDER</i>)<br>
<br>
<h1>Details</h1>

<h3>The default config file</h3>

<pre><code>###################################################<br>
############## WORKFLOW MODULE CONFIGURATION FILE ##############<br>
#########################################################<br>
#########################################################<br>
<br>
#### This file allows to configure the main property of the workflow module<br>
#### The language used is nameOfTheProperty = valueOfTheProperty<br>
#### '#' indicates comments<br>
#### empty line are ignored<br>
<br>
<br>
#####################<br>
# Main configuration variable #<br>
#####################<br>
<br>
# The name of the type of file where information about available workflows are described<br>
WORKLFOWLIST_TYPE = workflowlist<br>
<br>
# The string that starts the description of a Pattern<br>
PATTERN_STARTING_SUBSTRING = {<br>
<br>
# The string that ends the description of a Pattern<br>
PATTERN_ENDING_SUBSTRING = }<br>
<br>
# The separator use in the workflow right config file to separate the list of default values.<br>
WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR = |<br>
<br>
# The name of the class to load as Available Workflow Collector<br>
AVAILABLEWORKFLOWCOLLECTOR_CLASSNAME = com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollector<br>
<br>
# The name of the class to load as OSWorkflowManager<br>
OSWORKFLOWMANAGER_CLASSNAME = com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithRightManagement<br>
<br>
# The path where the search of "workflowlist" files will starts from<br>
WORKFLOWLIST_CONFIGFILE_FOLDER = /system/modules/com.eurelis.opencms.workflows/workflows/<br>
<br>
########################<br>
# Additional configuration variable #<br>
########################<br>
<br>
### Be careful :<br>
### By modifying those variables you may inactive the parsers used by the module<br>
<br>
# The context of the jaxb parser for file of type workflowlist<br>
PARSER_JAXBCONTEXT_LISTWORKFLOW = com.eurelis.opencms.workflows.jaxb.listworkflow<br>
<br>
# The context of the jaxb parser for file storing the configuration/rights associated to a workflow <br>
PARSER_JAXBCONTEXT_WORKFLOWCONFIG = com.eurelis.opencms.workflows.jaxb.workflowconfig<br>
<br>
# The context of the jaxb parser for file storing the information associated to an instance of workflow (stored in the db)<br>
PARSER_JAXBCONTEXT_WORKFLOWPROPERTYCONTAINER = com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer <br>
</code></pre>

<h3>Details about fields</h3>

The most important entries of this file are:<br>
<ul><li><b>WORKLFOWLIST_TYPE</b> : the name of the type of the files that will content the list of available workflows<br>
</li><li><b>WORKFLOWLIST_CONFIGFILE_FOLDER</b> : the folder where file of type indicate by WORKFLOWLIST_TYPE will be look for (Be carefull:  don't forget the / at the end of the path)</li></ul>

The other entries define the module behaviour:<br>
<ul><li><b>PATTERN_STARTING_SUBSTRING and PATTERN_ENDING_SUBSTRING</b> are the string that will be check to know if the Branch/User/Group/Role are pattern or not in the config file of workflow.<br>
</li><li><b>WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR</b> is the String that split the value when an entry can be a list (ex: the value of a property, or a workflow initial parameter)<br>
</li><li><b>AVAILABLEWORKFLOWCOLLECTOR_CLASSNAME</b> the name of the class dynamically loaded by the available workflow collector (this class go through the folder WORKFLOWLIST_CONFIGFILE_FOLDER, look for file of type WORKLFOWLIST_TYPE and parse them).<br>
</li><li><b>OSWORKFLOWMANAGER_CLASSNAME</b> the name of the dynamically loaded class used to manage the configurated workflow rights.</li></ul>

The last three entries define the context used by JAXB parsers :<br>
<ul><li><b>PARSER_JAXBCONTEXT_LISTWORKFLOW</b> is the parser for  "listworkflow" files. The xml scheme of this type of file is available in  <i>/system/modules/com.eurelis.opencms.workflows/schemas/listworkflow.xsd</i>
</li><li><b>PARSER_JAXBCONTEXT_WORKFLOWCONFIG</b> is the context to parse the workflow config file.  The xml scheme of this type of file is available in  <i>/system/modules/com.eurelis.opencms.workflows/schemas/allowance.xsd</i>
</li><li><b>PARSER_JAXBCONTEXT_WORKFLOWPROPERTYCONTAINER</b> is the context to parse the XML code stored as property of the workflow. The xml scheme of this type of file is available in  <i>/system/modules/com.eurelis.opencms.workflows/schemas/workflowPropertyContainer.xsd</i>