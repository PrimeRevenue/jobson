Job Specs
=========


Overview
--------

Job specs are saved in the job specs directory (e.g.
``specs/{spec-id}/spec.yml``). At runtime, Jobson uses the job spec to
figure out a job's:

-  ``name:`` and ``description:``
-  ``expectedInputs:`` from clients
-  ``execution:`` of the ``application:``, once the inputs are provided
-  ``execution:`` ``arguments:``, which may include some of the inputs
-  ``execution:`` ``dependencies:``, such as scripts or data files that
   should be copied into the runtime working directory
-  ``expectedOutputs:`` from the application, which Jobson should
   persist

A basic example of a job spec would be:

.. literalinclude:: _static/demo-job-spec.yml
    :linenos:
    :language: yaml

This example specifies:

-  A job with a human-readable ``name:`` of ``Demo   Job Spec`` (and
   ``description:``).

-  That expects clients to provide a single string input called
   ``firstName``, which defaults to ``Jeff`` if clients don't provide it

-  That, once it receives the inputs:

-  Copies ``demo-script.sh`` from the job spec's directory to
   ``demo-script.sh`` in the job's working directory
-  Executes ``bash`` with ``demo-script.sh`` and ``${inputs.firstName}``
   with two
-  arguments: - ``demo-script.sh``: The script ``bash`` should run -
-  ``${inputs.firstName}``: The ``firstName`` provided as an input -
-  Before executing, Jobson should copy ``demo-script.sh`` from the job
-  spec folder to the job's working directory -

This specifies a job that takes a single string input (``firstName``)
and runs a bash script (``demo-script.sh``). The bash script is copied
from the spec directory (``dependencies: source: demo-script.sh``) into
the job's working directory (``dependencies: target: demo-script.sh``).
Once the script runs, it produces a file called ``output``, which has an
``image/jpeg`` MIME type and should be persisted as
``${request.id}.jpg`` by Jobson.

Creating a Job Spec
-------------------

The Jobson command-line interface comes with a basic spec generator for
creating a bareboes spec:

.. code:: bash

    $ jobson generate spec new-spec

This creates a job spec with an ID of ``new-spec`` at
``jobs/new-spec/spec.yml``.

``spec.yml`` Schema
-------------------

Job specs are standard YAML files that should appear at the correct path
(e.g. ``jobs/{job-id}/spec.yml``). This section describes what can be
put into the ``spec.yml`` files.

``name``
~~~~~~~~

Name of the Job Spec.

``description``
~~~~~~~~~~~~~~~

Detailed Description of the Job Spec

``expectedInputs``
~~~~~~~~~~~~~~~~~~

What Input Data Should be Provided to Execute a Job

``execution``
~~~~~~~~~~~~~

What Executes Once the Input Data is Received.

``expectedOutputs``
~~~~~~~~~~~~~~~~~~~

What is Produced by Execution (and should be persisted by Jobson).

TODO: Work in progress. Need documentation about supported data types,
scripting functions, etc.


Template Strings
----------------

Template strings, such as ``${request.id}`` allow developers to add
runtime evaluation to their job specs. For example, template strings
allow runtime input arguments to be used as application arguments:

.. code:: yaml

    execution:
      application: bash
      arguments:
      - ${inputs.firstName}

This is the main way of passing client inputs into an application at
runtime.

Template strings are ordinary strings with support for special
expressions between ``${`` and ``}``. Jobson interprets those
expressions at runtime, evaluating them against an environment
containing data (e.g. ``inputs``, ``request``) and functions (e.g.
``toJSON``, ``toFile``).

Template strings have access to the following:

``String toJSON(Object arg)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A function that takes an input (e.g. ``request``, ``request.id``,
``'string literal'``) and returns a JSON representation of the input.
Object-/Hash-like arguments are converted into a JSON object, strings
are converted into a JSON string, numbers into JSON numbers, etc.

``String toFile(String content)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A function that takes a string input (e.g. ``request.id``,
``toJSON(request)``), write the input to a file, and returns an absolute
path to that file.

``String join(String delimiter, StringArray strings)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A function that takes a delimiter (e.g. ``','``) and a ``StringArray``
(usually, from a Jobson ``string[]`` ``expectedInput``) and returns a
string that contains each element in the ``StringArray`` joined with the
delimiter.

``String toString(Object arg)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A function that takes an argument (e.g. ``request.firstName``) and
returns a string representation of that arugment. Note: internally, this
uses the Java ``.toString`` method on the argument, which may produce
non-obvious results with complex arguments.

``Object request``
~~~~~~~~~~~~~~~~~~

A reference to the request that initiated the job. Useful fields in the
request are:

-  ``String id``: The job's ID (e.g. ``57s9fmopb``)
-  ``String owner``: The client that initaited the job request (e.g.
   ``foouser``)
-  ``String name``: The name the client given the job
-  ``Map<JobExpectedInputId, JobInput> inputs``: The inputs given for
   the job.
-  Each input is accessible via the dot accessor (e.g.
   ``request.inputs.firstName``).
-  The ``Map`` is serializable as JSON (e.g.
   ``toJSON(request.inputs)``).
-  ``JobSpec spec``: The job spec the job was submitted against

``Object inputs``
~~~~~~~~~~~~~~~~~

Shortcut for ``request.inputs``.

``String outputDir``
~~~~~~~~~~~~~~~~~~~~

The output directory for the job. At time of writing, this is always the
same as the job's working directory.
