<h1>Details about the functions developed by Eurelis for OSWorkflow<h1>

<h1>Introduction</h1>

The following functions have been developed for the two provided workflows:<br>
<ul><li>The task attribution workflow<br>
</li><li>The approbation workflow</li></ul>


Each of them correspond to a java class stored in the package_com.eurelis.opencms.workflows.functions<br>
<br>
Here is the class diagram about theses functions<br>
<br>
<img src='http://eurelis-opencms-workflows.googlecode.com/svn/wiki/images/DiagrammeDeClasseWorkflowEurelisFunctions.png' />


<h1>Details</h1>

<h2>Add new functions</h2>

It's quite easy to add new function that could be use by OSWorkflow. You just have to create a new class that implements the interface <b>com.opensymphony.workflow.FunctionProvider</b>. This interface required the implementation of the method <i>execute()</i>

This execute() method has three paramateres:<br>
<ul><li>A Map<String,Object> <i>transcientVar</i> that store the all variables that are passed as parameters when calling method <i>initialize()</i> or <i>doAction()</i> on a workflow object (cf <a href='http://www.opensymphony.com/osworkflow/api/'>OSWorkflow API</a>). This two method respectively create an instance of workflow and cross a transition.<br>
</li><li>A Map<String,String> <i>args</i> where are stored the arguments (and there values) describe in the OSWorkflow workflow description file (cf <i>arg</i> tag associated to each function <a href='http://wiki.opensymphony.com/display/WF/1.+Your+first+workflow'>OSWorflow documentation</a>)<br>
</li><li>A PropertySet object. This object is associated to each instance of OSWorkflow and stored in DB. This object is used (in our implementation) to store an XML content corresponding to serialized version of a WorkflowPropertyContainer instance. This WorkflowPropertyContainer object stored all information relative to the ongoing workflow (history, author, associated files)</li></ul>

<h2>Our developed functions</h2>

<h3>EurelisWorkflowFunction</h3>

This is the mother class of all the following functions. It implements the interface <b>FunctionProvider</b>. This class collect the informations required by subclasses.<br>
<br>
The <i>execute()</i> function does a few thinks :<br>
<ol><li>Initialized the class variables (most are extracted from the PropertySet instance, the other come from parameters values)<br>
</li><li>call <b>executeFuncton()</b>, an abstract method that must be implemented and that is in charge to really execute the required function<br>
</li><li>Update the WorkflowPropertyContainer object associated to the instance of workflow</li></ol>

Here are the elements collected during initialization :<br>
<ul><li>The list of associated files<br>
</li><li>The instance of CmsObject<br>
</li><li>The current user name<br>
</li><li>The user comment<br>
</li><li>The list of parameters values enter by the user when it created the instance<br>
</li><li>A static comment(used if the action is done automatically) . This element is set in the workflow description file with "comment" argument name (not mandatory).<br>
</li><li>A static user name (used if the action is done automatically) . This element is set in the workflow description file with "actor" argument name (not mandatory).</li></ul>

<h3>JustFillPropertyFunction</h3>

This function do nothing special except add a new entry in the history. You should do it to store user action in the history.<br>
<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.JustFillPropertyFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="comment"&gt;YourComment&lt;/arg&gt;<br>
   &lt;arg name="actor"&gt;YourActorName&lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>SelectFileRecursivelyFunction</h3>

This function allows to modulate the list of selected files according to user parameters. It take the list of selected used files and apply the user wish to get the final list of selected files.<br>
<br>
This method has three parameters (optional, default value is false) :<br>
<ul><li><b>SelectFileRecursively</b> indicate if a recursive algorithm must be used<br>
</li><li><b>SelectAssociatedFile</b> indicate if the associated files must be add to the list<br>
</li><li><b>CheckOffline</b> indicate if only Offline files must be add to the list</li></ul>

So as to used this function, the parameters must be describes in the workflow rights configuration file (cf <a href='DescribeWorkflowRightsAndParameters.md'>How to describes parameters</a>) with the following code:<br>
<pre><code>&lt;params&gt;<br>
   &lt;param name="SelectFileRecursively" type="boolean" display="Select files recursively" default="true"/&gt;<br>
   &lt;param name="SelectAssociatedFile" type="boolean" display="Select associated files"/&gt;<br>
   &lt;param name="CheckOffline" type="boolean" display="Select only offline files"/&gt;	<br>
&lt;/params&gt;<br>
</code></pre>

Then you can use the function with the following code :<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.SelectFileRecursivelyFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="comment"&gt;YourComment&lt;/arg&gt;<br>
   &lt;arg name="actor"&gt;YourActorName&lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>MoveToProjectFunction</h3>

This function move the list of associated files to another project. The required parameters is the name of the target project.<br>
<br>
This is done with the parameter "<b>destination</b>".<br>
<br>
<b>BE CAREFULL</b> : this function must not be use to move the files to the project <i>Online</i>. Use <a href='EurelisFunctionsForOSWOrkflow#PublishResourcesFunction.md'>PublishResourcesFunction</a> instead.<br>
<br>
Here is the way to use this function:<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;	<br>
      com.eurelis.opencms.eurelisworkflows.functions.MoveToProjectFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="destination"&gt;Staging&lt;/arg&gt;<br>
   &lt;arg name="comment"&gt;YourComment&lt;/arg&gt;<br>
   &lt;arg name="actor"&gt;YourActorName&lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>PublishResourcesFunction</h3>

This function publish the list of files associated to the workflow.<br>
<br>
Use it with :<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.PublishResourcesFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="comment"&gt;YourComment&lt;/arg&gt;<br>
   &lt;arg name="actor"&gt;YourActorName&lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>ChangeLockOnResourcesFunction</h3>

This function allows to change the lock value on the all files associated to the workflow.<br>
<br>
The required parameters is the target value of the lock. This value is set statically in workflow description file with an argument called "<b>lockValue</b>"<br>
<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.ChangeLockOnResourcesFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="lockValue"&gt;false&lt;/arg&gt;<br>
   &lt;arg name="comment"&gt;YourComment&lt;/arg&gt;<br>
   &lt;arg name="actor"&gt;YourActorName&lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>MoveToProjectAndSetLockFunction</h3>

This function gather the functionality of <a href='EurelisFunctionsForOSWOrkflow#MoveToProjectFunction.md'>MoveToProjectFunction</a> and <a href='EurelisFunctionsForOSWOrkflow#ChangeLockOnResourcesFunction.md'>ChangeLockOnResourcesFunction</a>.<br>
So it required both "<b>lockValue</b>" and "<b>destination</b>" parameters as describes before.<br>
<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.ChangeLockOnResourcesFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="lockValue"&gt;false&lt;/arg&gt;<br>
   &lt;arg name="destination"&gt;Staging&lt;/arg&gt;<br>
   &lt;arg name="comment"&gt;YourComment&lt;/arg&gt;<br>
   &lt;arg name="actor"&gt;YourActorName&lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>EmailNotificationFunction</h3>

This function allow to send a notification to the users. So has to works well, the SMTP must be configured (cf <a href='HowToInstallTheModule#Mailhost_management.md'>Mailhost management</a>).<br>
<br>
The list of parameters is the following :<br>
<ul><li>The list of receivers, set in the workflow description file with parameter "<b>receivers</b>"<br>
</li><li>The body of the email (optional), set in the workflow description file with parameter "<b>email_body</b>"<br>
</li><li>The subject of the email (optional), set in the workflow description file with parameter "<b>email_subject</b>"</li></ul>

<h4>Receivers configuration</h4>

The "receivers" value is a list of people, separated with ';'. Each of theses receivers can be:<br>
<ul><li><i>An email address</i>
</li><li><i>A value of the current workflow property</i> (only "author" is available for now) : use the prefix <b>instance:</b> before the property name. (ex: instance:author -> Send the notification to the creator of the workflow )<br>
</li><li><i>An OpenCms property associated to the files</i> : use the prefix <b>opencms:</b> before the Opencms Property value to use. (ex: opencms:reviewer ->Send a notification to the reviewer of the files)<br>
</li><li><i>The value of the creation parameters of the workflow instance</i> : use the prefix <b>parameters:</b> before the parameter name (ex: parameter:receivers -> The value of the parameter <i>receivers</i> entered when creating the instance of workflow)</li></ul>

<b>NB</b>: The OpenCms property or the parameter must be filled with a list of OpenCms login. Each login must be associated to a valid email address.<br>
<br>
Example: toto@eurelis.com;titi@myCompagny.com;opencms:reviewer;parameter:receivers;instance:author<br>
<br>
<h4>Email body configuration</h4>

The email body must contains key values that will be updated when sending the message :<br>
<ul><li><b><code>[[</code>initialcomment<code>]]</code></b> : The creation comment<br>
</li><li><b><code>[[</code>lastcomment<code>]]</code></b> : The last entered comment<br>
</li><li><b><code>[[</code>lastlastcomment<code>]]</code></b> : The last last entered comment<br>
</li><li><b><code>[[</code>creator<code>]]</code></b> : The login of the person that has create the workflow<br>
</li><li><b><code>[[</code>lastactor<code>]]</code></b> : The login of the person that has perform the last action<br>
</li><li><b><code>[[</code>lastlastactor<code>]]</code></b> : The login of the person that has perform the last last action</li></ul>


Here is the way of using this function:<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.EmailNotificationFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="receivers"&gt;<br>
      s.bianco@eurelis.com;sebastien.bianco@gmail.com;opencms:reviewer<br>
   &lt;/arg&gt;<br>
   &lt;arg name="email_body"&gt;<br>
      Le(s) fichier(s) suivant vous ont ete propose a validation par [[creator]] : [[initialcomment]]<br>
   &lt;/arg&gt;<br>
   &lt;arg name="email_subject"&gt;<br>
      [Validation] Des fichiers sont en demande de validation.<br>
   &lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

<h3>ChangeAllowedPeopleFunction</h3>

This function allow to modify dynamically the rights associated to the current instance of the workflows. It overrides all previous defined rights. <b>Priority is given to allowance</b>.<br>
<br>
This function required the list of change for rights as parameters. This paremeters is name "<b>ChangeToApply</b>".<br>
<br>
The value of changeToApply must be a list of element separated with a ";". Each element must follows this format : <b>propertyType:userGroupRole:nameOrProperty:newRight</b>

<b>NB</b>: This function works only if the policy that use dynamic rights is use (cf <a href='ConfigureRightPolicy#The_developed_policy.md'>The developed policy</a> -> OSWorkflowManagerWithDynamicRightManagement )<br>
<br>
<h4>propertyType</h4>

The type of property that will contains the list of User/Role/Group on which changes must be done. Here are the allowed value :<br>
<ul><li><b>opencmcs</b> the property is an OpenCms object<br>
</li><li><b>instance</b> the property is relative to an instance of the workflow (only <i>author</i> - the instance creator  - and <i>lastactor</i> - the person that made the last action - are available now)<br>
</li><li><b>parameter</b> the property is a value passed as parameter when creating the instance of workflow<br>
</li><li><b>property</b> the property is the value of an OpenCms property relative to associated files</li></ul>


<h4>userGroupRole</h4>

Indicate to which kind of object the changes will be appied :<br>
<ul><li><b>u</b> the changes are applied on user<br>
</li><li><b>g</b> the changes are applied on group<br>
</li><li><b>r</b> the changes are applies on roles</li></ul>

<h4>nameOrProperty</h4>

The name of the user/group/role on which changes will be applied. It's so possible to use regular expression between "{" and "}" (cf <a href='DescribeWorkflowRightsAndParameters#The_users_rights_section.md'>How to configure users rights</a>).<br>
<br>
<h4>newRight</h4>

This string contains the description of the rights to add (nothing). This new rights override totally previously defined rights (statically or dynamically). "r" give the read rights, "w" give the write rights, "rw" both, "<i>" remove all rights.</i>

<h4>A few examples</h4>

<ul><li><i>opencms:u:Toto:rw</i> : the user with login "Toto" receive read/write rights<br>
</li><li><i>opencms:r:Root Administrator:w</i> : All person with role "Root Administrator" receive write rights.<br>
</li><li><i>opencms:u:{^a<code>*</code>$}:r</i> : All user with login starting with "a" receive the read rights<br>
</li><li><i>opencms:g:{^a<code>*</code>$}:<code>_</code></i> : All user that are members of a group which name starts with "a" are denied of rights<br>
</li><li><i>instance:u:author:r</i> : The creator of the workflow instance receive read rights<br>
</li><li><i>parameter:u:receivers:rw</i> :  All user which login have been listed in the parameter "receivers" when starting the instance of the workflow receive read/write rights<br>
</li><li><i>property:u:reviewer:rw</i> : All user which login have been listed in the property "reviewer" relative to a file attached to a workflow receive the read/write rights</li></ul>

Here is the way to use this function :<br>
<br>
<pre><code>&lt;function type="class"&gt;<br>
   &lt;arg name="class.name"&gt;<br>
      com.eurelis.opencms.eurelisworkflows.functions.ChangeAllowedPeopleFunction<br>
   &lt;/arg&gt;<br>
   &lt;arg name="changeToApply"&gt;<br>
      instance:u:author:r;property:u:reviewer:rw<br>
   &lt;/arg&gt;<br>
&lt;/function&gt;<br>
</code></pre>

