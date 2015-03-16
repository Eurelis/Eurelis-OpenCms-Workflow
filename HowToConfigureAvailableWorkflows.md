<h1>Configure the list of available workflows<h1>

<h1>Introduction</h1>

The list of available workflows can be configure easily :<br>
<ol><li>Write an workflow, using OSWorkflow syntax <a href='http://www.opensymphony.com/osworkflow/documentation.action'>OS Workflow Documentation</a>
</li><li>Write the file to describes parameters and users rights (cf <a href='DescribeWorkflowRightsAndParameters.md'>DescribeWorkflowRightsAndParameters</a>)<br>
</li><li>Configure the available workflows thanks to a file that links the two files</li></ol>

<h1>Details</h1>

So as to configure the list of workflows that will be available in the all application, you should create <b>one or more</b> file of type "workflowlist" in the folder specified in <i>WORKFLOWLIST_CONFIGFILE_FOLDER</i> of module config file (cf <a href='BasicConfigurationOfTheModule.md'>BasicConfigurationOfTheModule</a>).<br>
(New > Structured content > List of Workflows)<br>
<br>
When you edit a file of this type, you get the following window:<br>
<br>
<br>
<img src='http://eurelis-opencms-workflows.googlecode.com/svn/wiki/images/fenetre_configuration_workflows_accessibles.png' />


You have to create as instance as workflows you want to allow access (for this, use the green plus available when clicking on the target).<br>
For each instance you need to define :<br>
<ul><li><b>Title</b> : the name of the workflow<br>
</li><li><b>Description</b> : the description that will be displayed in front of the name when create an instance of workflow<br>
</li><li><b>WF File</b> : the path of the XML workflow description file (OSWorkflow language).<br>
</li><li><b>WF Rights</b> : the path of the XML workflow configuration file. This file contains the description of the user rights and of the initial parameter of the workflow. (cf next section)