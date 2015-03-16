<h1>How to configure a type of workflow - Create a configuration file<h1>

<h1>Introduction</h1>

Each workflow is associated to a file that describes its relating rights and the list of parameters that will be consumed during the workflow execution<br>
<br>
This article will describes this files.<br>
<br>
<h1>Details</h1>

<h2>Document structure</h2>

The workflow description file structure is the following :<br>
<br>
<pre><code>&lt;workflow name="ValidationWorkflow"&gt;<br>
<br>
   &lt;params&gt;<br>
      &lt;param name="SelectFileRecursively" type="boolean" display="Select files recursively" default="true"/&gt;<br>
      &lt;param name="SelectAssociatedFile" type="boolean" display="Select associated files"/&gt;<br>
      &lt;param name="CheckOffline" type="boolean" display="Select only offline files"/&gt;<br>
   &lt;/params&gt;<br>
<br>
   &lt;rights&gt;<br>
      &lt;branch path="/"&gt;<br>
         &lt;allowRole name="Root administrator" type="rw"/&gt;<br>
         &lt;allowGroup name="{^Admin.*}" type="rw"/&gt;	<br>
      &lt;/branch&gt;<br>
<br>
      &lt;branch path="{^/sites/default/.*$}"&gt;<br>
         &lt;allowUser name="bbbb" type="r"/&gt;<br>
         &lt;allowUser name="cccc" type="w"/&gt;<br>
      &lt;/branch&gt;<br>
 <br>
      &lt;branch path="NO_FILES"&gt;<br>
         &lt;allowRole name="Root administrator" type="rw"/&gt;<br>
         &lt;allowGroup name="{^Admin.*}" type="rw"/&gt;	<br>
      &lt;/branch&gt;<br>
   &lt;/rights&gt;<br>
<br>
&lt;/workflow&gt;<br>
</code></pre>

It is divided in two parts :<br>
<ul><li>The <b>params</b> part is the one where are define the initial parameters of the workflow<br>
</li><li>The <b>rights</b> part allow to define the rights associated to each user. This rights are configured by branch, i.e per section of the virtual file system.</li></ul>

<u>Note:</u>  the name associated to the "workflow" tag is ignored by the system.<br>
<br>
<h2>The parameters section</h2>

Here is a more exaustif example of what you can do with initial parameters:<br>
<pre><code>&lt;params&gt;<br>
   &lt;param name="AStringWithDefault" type="String" display="String" default="true"/&gt;<br>
   &lt;param name="AListStringWithDefault" type="String" display="List of String" default="a;b;c;d"/&gt;<br>
   &lt;param name="AListOfShort" type="short" display="a list of short" default="1;2;3;4"/&gt;<br>
   &lt;param name="AChar" type="char" display="A character" default="c"/&gt;<br>
   &lt;param name="AInt" type="int" display="A integer" default="1"/&gt;<br>
   &lt;param name="AInt2" type="int" display="A integer without default"/&gt;<br>
   &lt;param name="AListInt" type="int" display="A list of integer" default="1;2;3;4"/&gt;<br>
&lt;params/&gt;<br>
</code></pre>

In <b>params</b> tag, you can put as many <b>param</b> instance as you want. Each of this tag represent an initial parameter that the user must complete when he instanciates a workflow<br>
<ul><li><b>name</b> is the system name of the parameter. This is the value used by the system to get what enter the user. This name is  not shown to the user and it is recommand to avoid special character or space. This attribut is <i><b>mandatory</b></i>.<br>
</li><li><b>type</b> is the type of parameter. It is use by the system to valid the entry of the user. The allowed value are Jave native type. This attribut is <i><b>mandatory</b></i>.<br>
</li><li><b>display</b> is the string displayed to the user. This attribut is <i><b>mandatory</b></i>.<br>
</li><li><b>default</b> is a list of default value seprated with semi-colon. This field is <i><b>optionnal</b></i>.</li></ul>

Here is the display corresponding to previous code:<br>
<br>
<img src='http://eurelis-opencms-workflows.googlecode.com/svn/wiki/images/listParamMyFirstWorkflow.png' />

Here is the one corresponding to the code of the previous section:<br>
<br>
<img src='http://eurelis-opencms-workflows.googlecode.com/svn/wiki/images/listParamValidationProcess.png' />

<h2>The users rights section</h2>

Here is an example:<br>
<br>
<pre><code>&lt;rights&gt;<br>
   &lt;branch path="/"&gt;<br>
      &lt;allowRole name="Root administrator" type="rw"/&gt;<br>
      &lt;allowUser name="{^bi.*$}" type="rw"/&gt;<br>
   &lt;/branch&gt;<br>
   &lt;branch path="{^/sites/default/allowed/all/.*$}"&gt;<br>
      &lt;allowUser name="bbbb" type="r"/&gt;<br>
      &lt;allowUser name="cccc" type="w"/&gt;<br>
      &lt;allowGroup name="MyGroup" type="rw"/&gt;<br>
      &lt;allowRole name="Users" type="r"/&gt;<br>
   &lt;/branch&gt;<br>
   &lt;branch path="NO_FILES"&gt;<br>
      &lt;allowUser name="bbbb" type="r"/&gt;<br>
      &lt;allowUser name="cccc" type="w"/&gt;<br>
      &lt;allowGroup name="MyGroup" type="rw"/&gt;<br>
      &lt;allowRole name="Users" type="r"/&gt;<br>
   &lt;/branch&gt;<br>
&lt;/rights&gt;<br>
</code></pre>

In <b>rights</b> tag (only one for the all document), each <b>branch</b> tag contains the description of rights for users (<b>allowUser</b>), roles (<b>allowRole</b>) or groups (<b>allowGroup</b>). You can put as much tag allowUser/allowRole and allowGroup as required. Theses can be shuffled.<br>
<br>
<h3>Rights definition</h3>

Each user define by attribut <i>name</i> are associated with right defined in attribut <i>type</i>.<br>
<br>
If type content :<br>
<ul><li><b>"r"</b> then the user can access the informations about instances of workflow.<br>
</li><li><b>"w"</b> then the user can make the instance of workflow progress.<br>
</li><li><b>"c"</b> then the user can create instance of workflow.</li></ul>

<h3>Use pattern instead...</h3>

The <i>path</i> of branch tag and the <i>name</i> attribut of tags allowUser, allowGroup and allowRole allow to use regular expression instead of basic one. Theses regular expressions must put inside brackets ("<b>{</b>" and "<b>}</b>") to be recognize and analysed by the system.<br>
<br>
<h3>If there is no associated files...</h3>

Rights are defined per branch. So the allowance are relative to the list of files associated to the instance of workflow. In some case, you can have no associated files. To define the rights of theses particular instances of workflow, use the keyword "<b>NO_FILES</b>" as value of attribut path for branch tag.<br>
<br>
<h3>Remarks</h3>

<ul><li>A user always has read rights on instance of workflow he created, but never had write rights (except by dynamic rights)<br>
</li><li>If no rights are define for a user, <b>the default value is forbidden</b>. Nevertheless, <b>priority is given to allowance</b>. So if a master branch define an allowance for a user, these one cannot be restreint by rights define on a subsection of the VFS.