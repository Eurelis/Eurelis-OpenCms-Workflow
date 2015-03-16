<h1>How to configure the rights policy<h1>

<h1>Introduction</h1>

The rights policy are currently "HardCoded". Nevertheless, many rights policy have been developed and can be used.<br>
It's quite simple: you just have to change the java class used in the basic config file (cf <a href='BasicConfigurationOfTheModule.md'>BasicConfigurationOfTheModule</a> property <b>OSWORKFLOWMANAGER_CLASSNAME</b>)<br>
<br>
<h1>Details</h1>

<h2>The developed policy</h2>

According to the value set for property OSWORKFLOWMANAGER_CLASSNAME, you will had different right management behaviour :<br>
<br>
<ul><li><b>com.eurelis.opencms.workflows.workflows.OSWorkflowManager</b> : This implementation doesn't take care of access right on workflows.<br>
</li><li><b>com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithRightManagement</b> :<br>
<ul><li>If there is no file attached to the WF, the rights applied are those of NO_FILES branch.<br>
</li><li>The creator of a workflow always has read rights on his workflow.<br>
</li><li>The other users follow the rule define statically in the config file.<br>
</li></ul></li><li><b>com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithRightManagementAndOwnerForbidden</b> :<br>
<ul><li>If there is no file attached to the WF, the rights applied are those of NO_FILES branch.<br>
</li><li>The creator of a workflow always has read rights on his workflow.<br>
</li><li>The creator of a workflow never has write rights on his workflow.<br>
</li><li>The other users follow the rule define statically in the config file.<br>
</li></ul></li><li><b>com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithDynamicRightManagement</b> :<br>
<ul><li>If there is no file attached to the WF, the rights applied are those of NO_FILES branch.<br>
</li><li>The creator of a workflow always has read rights on his workflow.<br>
</li><li>The creator of a workflow never has write rights on his workflow.<br>
</li><li>The other users follow the rule define statically in the config file.<br>
</li><li>If there is allowed people define for the instance of workflow, this one take priority on rights statically defined. This dynamic right system is only use to calculate write access.</li></ul></li></ul>

The default value of the property is <i>com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithDynamicRightManagement</i>

<h2>Add more policy</h2>

All the policy extends the abstract class <b>com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager</b>. You can add your own policy by creating your own implementation of the class.<br>
Only four methods must be defined :<br>
<br>
<pre><code>        /**<br>
	 * Check if the current user has right to see this workflow<br>
	 * <br>
	 * @param key<br>
	 *            the key of the workflow<br>
	 * @param cmsRequestContext<br>
	 *            the request context<br>
	 * @param parentFolder<br>
	 *            the path of file<br>
	 * @param propertyContainer<br>
	 *            the property container of the instance of workflow<br>
	 * @return &lt;i&gt;true&lt;/i&gt; if the user is allow to see the workflow, &lt;i&gt;false&lt;/i&gt; otherwise.<br>
	 */<br>
	protected abstract boolean hasReadRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,<br>
			String parentFolder, WorkflowPropertyContainer propertyContainer);<br>
<br>
	/**<br>
	 * Check if the current user has right to see this workflow<br>
	 * <br>
	 * @param key<br>
	 *            the key of the workflow<br>
	 * @param cmsRequestContext<br>
	 *            the request context<br>
	 * @param parentFolder<br>
	 *            the path of file<br>
	 * @param propertyContainer<br>
	 *            the property container of the instance of workflow<br>
	 * @return &lt;i&gt;true&lt;/i&gt; if the user is allow to see the workflow, &lt;i&gt;false&lt;/i&gt; otherwise.<br>
	 */<br>
	protected abstract boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,<br>
			String parentFolder, WorkflowPropertyContainer propertyContainer);<br>
<br>
	/**<br>
	 * Check that the current user is the owner of the workplace. If this method return true, the own workflows of the<br>
	 * creator will be displayed whatever its rights.<br>
	 * <br>
	 * @param propertyContainer<br>
	 *            the container of the property of the workflow<br>
	 * @param name<br>
	 *            the name of the current user<br>
	 * @return &lt;i&gt;true&lt;/i&gt; if the user is the owner of the instance of workflow, &lt;i&gt;false&lt;/i&gt; otherwise<br>
	 */<br>
	protected abstract boolean checkOwner(WorkflowPropertyContainer propertyContainer, String name);<br>
<br>
	/**<br>
	 * Check if the current user has right to create this workflow<br>
	 * <br>
	 * @param key<br>
	 *            the key of the workflow<br>
	 * @param cmsRequestContext<br>
	 *            the request context<br>
	 * @param listOfFiles<br>
	 *            the list of initially selected files<br>
	 * @return &lt;i&gt;true&lt;/i&gt; if the user is allow to see the workflow, &lt;i&gt;false&lt;/i&gt; otherwise.<br>
	 */<br>
	protected abstract boolean hasCreateRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,<br>
			List&lt;String&gt; fileList);<br>
<br>
</code></pre>

The WorkflowKey object contains the name of the selected workflow.<br>
The WorkflowPropertyContainer object contains the list of Rights per branches.