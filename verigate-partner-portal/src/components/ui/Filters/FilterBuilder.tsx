import * as React from "react";
import { cn } from "@/lib/cn";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { X, Plus } from "lucide-react";

export type FilterOperator = "equals" | "contains" | "startsWith" | "endsWith" | "greaterThan" | "lessThan" | "between" | "in" | "notIn";

export interface FilterCondition {
  id: string;
  field: string;
  operator: FilterOperator;
  value: string | string[];
  type?: "text" | "number" | "date" | "select";
}

export interface FilterGroup {
  id: string;
  logic: "AND" | "OR";
  conditions: FilterCondition[];
}

interface FilterBuilderProps {
  groups: FilterGroup[];
  onGroupsChange: (groups: FilterGroup[]) => void;
  availableFields: Array<{
    key: string;
    label: string;
    type: "text" | "number" | "date" | "select";
    options?: Array<{ value: string; label: string }>;
  }>;
  className?: string;
}

const operatorLabels: Record<FilterOperator, string> = {
  equals: "equals",
  contains: "contains",
  startsWith: "starts with",
  endsWith: "ends with",
  greaterThan: "greater than",
  lessThan: "less than",
  between: "between",
  in: "is one of",
  notIn: "is not one of",
};

const operatorsByType: Record<string, FilterOperator[]> = {
  text: ["equals", "contains", "startsWith", "endsWith", "in", "notIn"],
  number: ["equals", "greaterThan", "lessThan", "between"],
  date: ["equals", "greaterThan", "lessThan", "between"],
  select: ["equals", "in", "notIn"],
};

export const FilterBuilder: React.FC<FilterBuilderProps> = ({
  groups,
  onGroupsChange,
  availableFields,
  className,
}) => {
  const addGroup = () => {
    const newGroup: FilterGroup = {
      id: `group-${Date.now()}`,
      logic: "AND",
      conditions: [],
    };
    onGroupsChange([...groups, newGroup]);
  };

  const removeGroup = (groupId: string) => {
    onGroupsChange(groups.filter(g => g.id !== groupId));
  };

  const updateGroup = (groupId: string, updates: Partial<FilterGroup>) => {
    onGroupsChange(
      groups.map(g => g.id === groupId ? { ...g, ...updates } : g)
    );
  };

  const addCondition = (groupId: string) => {
    const firstField = availableFields[0];
    const newCondition: FilterCondition = {
      id: `condition-${Date.now()}`,
      field: firstField.key,
      operator: operatorsByType[firstField.type][0],
      value: "",
      type: firstField.type,
    };

    updateGroup(groupId, {
      conditions: [
        ...groups.find(g => g.id === groupId)!.conditions,
        newCondition
      ]
    });
  };

  const removeCondition = (groupId: string, conditionId: string) => {
    const group = groups.find(g => g.id === groupId)!;
    updateGroup(groupId, {
      conditions: group.conditions.filter(c => c.id !== conditionId)
    });
  };

  const updateCondition = (groupId: string, conditionId: string, updates: Partial<FilterCondition>) => {
    const group = groups.find(g => g.id === groupId)!;
    updateGroup(groupId, {
      conditions: group.conditions.map(c => 
        c.id === conditionId ? { ...c, ...updates } : c
      )
    });
  };

  return (
    <div className={cn("space-y-4", className)}>
      <div className="flex items-center justify-between">
        <h3 className="text-aws-heading-s font-medium">Advanced Filters</h3>
        <Button
          variant="secondary"
          size="sm"
          onClick={addGroup}
          className="gap-2"
        >
          <Plus className="h-4 w-4" />
          Add Group
        </Button>
      </div>

      {groups.length === 0 ? (
        <div className="text-center py-8 text-text-muted">
          <p>No filters applied</p>
          <Button
            variant="secondary"
            size="sm"
            onClick={addGroup}
            className="mt-2"
          >
            Add your first filter group
          </Button>
        </div>
      ) : (
        <div className="space-y-4">
          {groups.map((group, groupIndex) => (
            <FilterGroupComponent
              key={group.id}
              group={group}
              groupIndex={groupIndex}
              availableFields={availableFields}
              onRemove={() => removeGroup(group.id)}
              onUpdate={(updates) => updateGroup(group.id, updates)}
              onAddCondition={() => addCondition(group.id)}
              onRemoveCondition={(conditionId) => removeCondition(group.id, conditionId)}
              onUpdateCondition={(conditionId, updates) => updateCondition(group.id, conditionId, updates)}
            />
          ))}
        </div>
      )}
    </div>
  );
};

interface FilterGroupComponentProps {
  group: FilterGroup;
  groupIndex: number;
  availableFields: Array<{
    key: string;
    label: string;
    type: "text" | "number" | "date" | "select";
    options?: Array<{ value: string; label: string }>;
  }>;
  onRemove: () => void;
  onUpdate: (updates: Partial<FilterGroup>) => void;
  onAddCondition: () => void;
  onRemoveCondition: (conditionId: string) => void;
  onUpdateCondition: (conditionId: string, updates: Partial<FilterCondition>) => void;
}

const FilterGroupComponent: React.FC<FilterGroupComponentProps> = ({
  group,
  groupIndex,
  availableFields,
  onRemove,
  onUpdate,
  onAddCondition,
  onRemoveCondition,
  onUpdateCondition,
}) => {
  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          <Badge variant="neutral" size="sm">
            Group {groupIndex + 1}
          </Badge>
          <select
            value={group.logic}
            onChange={(e) => onUpdate({ logic: e.target.value as "AND" | "OR" })}
            className="aws-select text-sm"
          >
            <option value="AND">AND</option>
            <option value="OR">OR</option>
          </select>
        </div>
        <Button
          variant="ghost"
          size="sm"
          onClick={onRemove}
          className="text-danger hover:text-danger"
        >
          <X className="h-4 w-4" />
        </Button>
      </div>

      <div className="console-card-body space-y-3">
        {group.conditions.map((condition, conditionIndex) => (
          <FilterConditionComponent
            key={condition.id}
            condition={condition}
            conditionIndex={conditionIndex}
            availableFields={availableFields}
            onRemove={() => onRemoveCondition(condition.id)}
            onUpdate={(updates) => onUpdateCondition(condition.id, updates)}
          />
        ))}

        <Button
          variant="secondary"
          size="sm"
          onClick={onAddCondition}
          className="gap-2"
        >
          <Plus className="h-4 w-4" />
          Add Condition
        </Button>
      </div>
    </div>
  );
};

interface FilterConditionComponentProps {
  condition: FilterCondition;
  conditionIndex: number;
  availableFields: Array<{
    key: string;
    label: string;
    type: "text" | "number" | "date" | "select";
    options?: Array<{ value: string; label: string }>;
  }>;
  onRemove: () => void;
  onUpdate: (updates: Partial<FilterCondition>) => void;
}

const FilterConditionComponent: React.FC<FilterConditionComponentProps> = ({
  condition,
  availableFields,
  onRemove,
  onUpdate,
}) => {
  const field = availableFields.find(f => f.key === condition.field);
  const availableOperators = field ? operatorsByType[field.type] : [];

  const handleFieldChange = (fieldKey: string) => {
    const newField = availableFields.find(f => f.key === fieldKey);
    if (newField) {
      const newOperator = operatorsByType[newField.type][0];
      onUpdate({
        field: fieldKey,
        operator: newOperator,
        type: newField.type,
        value: "",
      });
    }
  };

  const renderValueInput = () => {
    if (!field) return null;

    if (field.type === "select" && field.options) {
      if (condition.operator === "in" || condition.operator === "notIn") {
        // Multi-select for "in" operators
        return (
          <select
            multiple
            value={Array.isArray(condition.value) ? condition.value : []}
            onChange={(e) => {
              const values = Array.from(e.target.selectedOptions, option => option.value);
              onUpdate({ value: values });
            }}
            className="aws-select"
          >
            {field.options.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        );
      } else {
        // Single select
        return (
          <select
            value={typeof condition.value === "string" ? condition.value : ""}
            onChange={(e) => onUpdate({ value: e.target.value })}
            className="aws-select"
          >
            <option value="">Select value...</option>
            {field.options.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        );
      }
    }

    if (condition.operator === "between") {
      const values = Array.isArray(condition.value) ? condition.value : ["", ""];
      return (
        <div className="flex gap-2">
          <input
            type={field.type === "date" ? "date" : field.type === "number" ? "number" : "text"}
            value={values[0] || ""}
            onChange={(e) => onUpdate({ value: [e.target.value, values[1] || ""] })}
            placeholder="From"
            className="aws-input flex-1"
          />
          <input
            type={field.type === "date" ? "date" : field.type === "number" ? "number" : "text"}
            value={values[1] || ""}
            onChange={(e) => onUpdate({ value: [values[0] || "", e.target.value] })}
            placeholder="To"
            className="aws-input flex-1"
          />
        </div>
      );
    }

    return (
      <input
        type={field.type === "date" ? "date" : field.type === "number" ? "number" : "text"}
        value={typeof condition.value === "string" ? condition.value : ""}
        onChange={(e) => onUpdate({ value: e.target.value })}
        placeholder="Enter value..."
        className="aws-input"
      />
    );
  };

  return (
    <div className="flex items-center gap-2 p-3 bg-background rounded-aws-control border border-border">
      <select
        value={condition.field}
        onChange={(e) => handleFieldChange(e.target.value)}
        className="aws-select min-w-[150px]"
      >
        {availableFields.map((field) => (
          <option key={field.key} value={field.key}>
            {field.label}
          </option>
        ))}
      </select>

      <select
        value={condition.operator}
        onChange={(e) => onUpdate({ operator: e.target.value as FilterOperator })}
        className="aws-select min-w-[120px]"
      >
        {availableOperators.map((op) => (
          <option key={op} value={op}>
            {operatorLabels[op]}
          </option>
        ))}
      </select>

      <div className="flex-1">
        {renderValueInput()}
      </div>

      <Button
        variant="ghost"
        size="sm"
        onClick={onRemove}
        className="text-danger hover:text-danger flex-shrink-0"
      >
        <X className="h-4 w-4" />
      </Button>
    </div>
  );
};