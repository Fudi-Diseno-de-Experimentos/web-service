package synera.centralis.api.iam.domain.model.queries;


import synera.centralis.api.iam.domain.model.valueobjects.Roles;

/**
 * Get role by name query
 * <p>
 *     This class represents the query to get a role by its name.
 * </p>
 * @param name the name of the role
 */
public record GetRoleByNameQuery(Roles name) {
}
