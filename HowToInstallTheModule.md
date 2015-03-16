<h1>The installation guide of the module<h1>

<h1>Introduction</h1>

The installation required many steps:<br>
<ol><li>The module installaton himself<br>
</li><li>The JNDI config required by the implementation of OSWorkflow<br>
</li><li>The DB Configuration (required table configurations)<br>
</li><li>The OpenCms Configuration (SMTP/Menus)</li></ol>

Here come the details about each steps.<br>
Please do not reload your application context until the end of the installation.<br>
<br>
<h1>Details</h1>

NB: In following document<br>
<ul><li>CATALINA_HOME is tomcat installation folder<br>
</li><li>OPENCMS_HOME is the folder of your OpenCms web-app</li></ul>

<h2>The module installation</h2>

It's the easier part of the process:<br>
<ol><li>Download the built module from the repository or compile it from sources (an Ant build file is provided, the generated module is in <i>module</i> folder<br>
</li><li>Use the OpenCms module installer</li></ol>

<h2>The JNDI config</h2>

Here is, from my point of view the most difficult part of the installation, because you need  to know how to configure your application server. The following steps are describe for Apache Tomcat.<br>
<ol><li>Put <i>mysql-connector-java-5.0.7-bin.jar</i> in the folder <b>CATALINA_HOME/common/lib</b> (tomcat 5.x) or <b>CATALINA_HOME/lib</b> (tomcat 6)<br>
</li><li>Configure the context of the application so as to define the JNDI name of the DB<br>
(Please update YourWebAppsPath, YourUserName, YourPassword, YourDBServer and YourDatabaseURL with your config parameters ;))</li></ol>


<h3>Tomcat 5.0</h3>

Update <b>CATALINA_HOME/conf/server.xml</b>
<pre><code>&lt;Context path="/YourWebAppsPath" docBase="/YourWebAppsPath"&gt;<br>
   &lt;Resource name="jdbc/opencms" scope="Shareable" type="javax.sql.DataSource" /&gt;<br>
      &lt;ResourceParams name="jdbc/opencms"&gt;<br>
      &lt;parameter&gt;<br>
         &lt;name&gt;username&lt;/name&gt;<br>
         &lt;value&gt;YourUserName&lt;/value&gt;<br>
      &lt;/parameter&gt;<br>
      &lt;parameter&gt;<br>
         &lt;name&gt;password&lt;/name&gt;<br>
         &lt;value&gt;YourPassword&lt;/value&gt;<br>
      &lt;/parameter&gt;<br>
      &lt;parameter&gt;<br>
         &lt;name&gt;driverClassName&lt;/name&gt;<br>
         &lt;value&gt;org.gjt.mm.mysql.Driver&lt;/value&gt;<br>
      &lt;/parameter&gt;<br>
      &lt;parameter&gt;<br>
         &lt;name&gt;url&lt;/name&gt;<br>
         &lt;value&gt;<br>
            jdbc:mysql://YourDBServer:3306/YourDatabaseURL?zeroDateTimeBehavior=convertToNull<br>
         &lt;/value&gt;<br>
      &lt;/parameter&gt;<br>
   &lt;/ResourceParams&gt;<br>
&lt;/Context&gt;<br>
</code></pre>


<h3>Tomcat 5.5</h3>

Create a new file <b>CATALINA_HOME/conf/Catalina/localhost/YourWebAppName.xml</b> and put the following content in:<br>
<pre><code>&lt;Context path="/YourWebAppsPath" docBase="YourWebAppsPath" debug="5" reloadable="false" crossContext="true"&gt;<br>
   &lt;Resource name="jdbc/opencms" <br>
      auth="Container" <br>
      type="javax.sql.DataSource" <br>
      maxActive="100" <br>
      maxIdle="30" <br>
      maxWait="10000" <br>
      username="YourUserName" <br>
      password="YourPassword" <br>
      driverClassName="com.mysql.jdbc.Driver" <br>
      url="jdbc:mysql://YourDBServer:3306/YourDatabaseURL?zeroDateTimeBehavior=convertToNull"/&gt;<br>
&lt;/Context&gt;<br>
</code></pre>

<h3>Tomcat 6.0</h3>

Update <b>CATALINA_HOME/conf/server.xml</b>
<pre><code>&lt;Context path="/YourWebAppsPath" docBase="YourWebAppsPath" debug="0" crossContext="true"&gt;<br>
   &lt;Resource name="jdbc/opencms" <br>
      auth="Container" <br>
      type="javax.sql.DataSource" <br>
      maxActive="100" <br>
      maxIdle="30" <br>
      maxWait="10000" <br>
      username="YourUserName" <br>
      password="YourPassword" <br>
      driverClassName="com.mysql.jdbc.Driver" <br>
      url="jdbc:mysql://YourDBServer:3306/YourDatabaseURL?zeroDateTimeBehavior=convertToNull"/&gt;<br>
&lt;/Context&gt;<br>
</code></pre>

Here is my configuration under this server :<br>
<pre><code>&lt;Host name="localhost"  appBase="webapps"<br>
            unpackWARs="true" autoDeploy="true"<br>
            xmlValidation="false" xmlNamespaceAware="false"&gt;<br>
				<br>
   &lt;Context path="/instance1" docBase="instance1" debug="0" crossContext="true"&gt;<br>
     &lt;Resource name="jdbc/opencms" <br>
      auth="Container" <br>
      type="javax.sql.DataSource" <br>
      maxActive="100" <br>
      maxIdle="30" <br>
      maxWait="10000" <br>
      username="root" <br>
      password="" <br>
      driverClassName="com.mysql.jdbc.Driver" <br>
      url="jdbc:mysql://localhost:3306/instance1?zeroDateTimeBehavior=convertToNull"/&gt;<br>
    &lt;/Context&gt;<br>
&lt;/Host&gt;<br>
</code></pre>

<h2>The DB Configurations</h2>

Create the required table with the following script (for MySQL)<br>
<br>
<pre><code>DROP TABLE IF EXISTS OS_PROPERTYENTRY;<br>
DROP TABLE IF EXISTS OS_CURRENTSTEP_PREV;<br>
DROP TABLE IF EXISTS OS_HISTORYSTEP_PREV;<br>
DROP TABLE IF EXISTS OS_CURRENTSTEP;<br>
DROP TABLE IF EXISTS OS_HISTORYSTEP;<br>
DROP TABLE IF EXISTS OS_WFENTRY;<br>
<br>
DROP TABLE IF EXISTS OS_MEMBERSHIP;<br>
DROP TABLE IF EXISTS OS_USER;<br>
DROP TABLE IF EXISTS OS_GROUP;<br>
DROP TABLE IF EXISTS OS_PROPERTYENTRY;<br>
<br>
CREATE TABLE OS_WFENTRY<br>
(<br>
   ID int,<br>
   NAME varchar(128),<br>
   STATE smallint,<br>
   primary key (ID)<br>
);<br>
<br>
CREATE TABLE OS_CURRENTSTEP<br>
(<br>
   ID int,<br>
   ENTRY_ID int,<br>
   STEP_ID smallint,<br>
   ACTION_ID smallint,<br>
   OWNER varchar(20),<br>
   START_DATE TIMESTAMP ,<br>
   FINISH_DATE TIMESTAMP ,<br>
   DUE_DATE TIMESTAMP ,<br>
   STATUS varchar(20),<br>
   CALLER varchar(20),<br>
   primary key (ID),<br>
   foreign key (ENTRY_ID) references OS_WFENTRY(ID)<br>
);<br>
<br>
CREATE TABLE OS_HISTORYSTEP<br>
(<br>
   ID int,<br>
   ENTRY_ID int,<br>
   STEP_ID smallint,<br>
   ACTION_ID smallint,<br>
   OWNER varchar(20),<br>
   START_DATE TIMESTAMP ,<br>
   FINISH_DATE TIMESTAMP ,<br>
   DUE_DATE TIMESTAMP ,<br>
   STATUS varchar(20),<br>
   CALLER varchar(20),<br>
   primary key (ID),<br>
   foreign key (ENTRY_ID) references OS_WFENTRY(ID)<br>
);<br>
<br>
CREATE TABLE OS_CURRENTSTEP_PREV<br>
(<br>
   ID int,<br>
   PREVIOUS_ID int,<br>
   primary key (ID, PREVIOUS_ID),<br>
   foreign key (ID) references OS_CURRENTSTEP(ID),<br>
   foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)<br>
);<br>
<br>
CREATE TABLE OS_HISTORYSTEP_PREV<br>
(<br>
   ID int,<br>
   PREVIOUS_ID int,<br>
   primary key (ID, PREVIOUS_ID),<br>
   foreign key (ID) references OS_HISTORYSTEP(ID),<br>
   foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)<br>
);<br>
<br>
CREATE TABLE OS_PROPERTYENTRY<br>
(<br>
   GLOBAL_KEY varchar(255),<br>
   ITEM_KEY varchar(255),<br>
   ITEM_TYPE smallint,<br>
   STRING_VALUE text,<br>
   DATE_VALUE TIMESTAMP ,<br>
   DATA_VALUE blob,<br>
   FLOAT_VALUE float,<br>
   NUMBER_VALUE numeric,<br>
   primary key (GLOBAL_KEY, ITEM_KEY)<br>
);<br>
<br>
CREATE TABLE OS_USER<br>
(<br>
   USERNAME varchar(128) NOT NULL,<br>
   PASSWORDHASH varchar(128),<br>
   PRIMARY KEY (USERNAME)<br>
);<br>
<br>
CREATE TABLE OS_GROUP<br>
(<br>
   GROUPNAME varchar(128) NOT NULL,<br>
   PRIMARY KEY (GROUPNAME)<br>
);<br>
<br>
CREATE TABLE OS_MEMBERSHIP<br>
(<br>
   USERNAME varchar(128),<br>
   GROUPNAME varchar(128),<br>
   primary key (USERNAME, GROUPNAME),<br>
   foreign key (USERNAME) references OS_USER(USERNAME),<br>
   foreign key (GROUPNAME) references OS_GROUP(GROUPNAME)<br>
);<br>
</code></pre>


<h2>OpenCms Configuration</h2>

<h3>Mailhost management</h3>

Here is the configuration of the SMTP use to send notifications during workflows executions. It's the basic OpenCms configuration, if you run the module on an in-use OpenCms, you probably can forget this part.<br>
<br>
In file <i>OPENCMS_HOME/WEB-INF/config/opencms-system.xml</i> configure your SMTP server with following lines:<br>
<pre><code>&lt;mail&gt;<br>
   &lt;mailfrom&gt;YourMail&lt;/mailfrom&gt;<br>
   &lt;mailhost name="YOUR.SMTP.SERVER" protocol="smtp" /&gt;<br>
&lt;/mail&gt;<br>
</code></pre>

<h3>Menu configuration</h3>

Here is the way to follow to add the workflow menu item on Workplace OpenCms pop-up menus.<br>
It can be boring to do, because you need to to the first following steps for each type of content that OpenCms allows.<br>
<br>
Update the <i>OPENCMS_HOME/WEB-INF/config/opencms-workspace.xml</i>
<ol><li>For each type of content that must have the workflow menu add the following lines<i>(XPath = opencms/workplace/explorertypes/explorertype(@name="<code>*</code>")/editoptions/contextmenu)</i>. This add a menu entry to access the workflow, when only one file is selected.<br>
<pre><code>&lt;entry key="GUI_EXPLORER_CONTEXT_WORKFLOWS_0"<br>
   uri="/system/workplace/workflows/listAvailableWorkflows.jsp"<br>
   rule="eurelisDisplayRule" /&gt;<br>
</code></pre>
</li><li>Add the following lines in multicontextmenu section <i>(XPath = /opencms/workplace/explorertypes/multicontextmenu)</i>. This add new entry for a muliple files selection.<br>
<pre><code>&lt;entry key="GUI_EXPLORER_CONTEXT_WORKFLOWS_0"<br>
   uri="/system/workplace/workflows/listAvailableWorkflows.jsp"<br>
   rule="eurelisDisplayRule" /&gt;<br>
</code></pre>
</li><li>Add the following lines in the rules section <i>(XPath = /opencms/workplace/explorertypes/menurules)</i>. This add the custom display rule.<br>
<pre><code>&lt;menurule name="eurelisDisplayRule"&gt;<br>
   &lt;menuitemrule class="com.eurelis.opencms.workflows.rules.EurelisWorkflowsDisplayRule"/&gt;<br>
&lt;/menurule&gt;<br>
</code></pre>